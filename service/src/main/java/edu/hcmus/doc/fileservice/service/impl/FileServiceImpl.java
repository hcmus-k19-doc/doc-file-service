package edu.hcmus.doc.fileservice.service.impl;

import static edu.hcmus.doc.fileservice.common.Constants.ALLOWED_FILE_TYPES;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.exception.FileAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.service.FileService;
import edu.hcmus.doc.fileservice.service.FolderService;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeChildAssociationEntry;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.alfresco.search.handler.SearchApi;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  private final SitesApi sitesApi;
  private final NodesApi nodesApi;

  private final SearchApi searchApi;

  private final FolderService folderService;

  @Override
  public List<String> getFileTitles() {
    return Collections.emptyList();
  }

  @Override
  public Boolean isValidFile(String fileName, String fileExtension, String parentFolderId) {
    // check if file already exists
    if (isFileExist(fileName, parentFolderId)) {
      throw new FileAlreadyExistedException(FileAlreadyExistedException.FILE_ALREADY_EXISTED);
    }
    // check if file type is allowed
    if (ALLOWED_FILE_TYPES.contains(fileExtension)) {
      return true;
    }
    return false;
  }

  @Override
  public Boolean isFileExist(String fileName, String parentFolderId) {
    NodeChildAssociationPagingList folderContent = folderService.getFolderContent(parentFolderId);
    if (folderContent.getEntries().size() > 0) {
      for (NodeChildAssociationEntry nodeEntry : folderContent.getEntries()) {
        if (nodeEntry.getEntry().getName().equals(fileName)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Node uploadFile(MultipartFile multipartFile, String parentFolderId) {
    // check if file already exists
    if (isFileExist(multipartFile.getOriginalFilename(), parentFolderId)) {
      throw new FileAlreadyExistedException(FileAlreadyExistedException.FILE_ALREADY_EXISTED);
    }

    // check if file type is allowed
    if (!ALLOWED_FILE_TYPES.contains(multipartFile.getContentType().split("/")[1].toLowerCase())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(parentFolderId, null, null,
        null).getBody()).getEntry();

    // Create the file node metadata
    Node fileNode = Objects.requireNonNull(nodesApi.createNode(parentFolderNode.getId(),
        new NodeBodyCreate().nodeType("cm:content").name(multipartFile.getOriginalFilename()),
        null, null, null, null, null).getBody()).getEntry();

    // Add the file node content
    Node savedFileNode = null;
    try {
      savedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
          multipartFile.getBytes(), true, null, null,
          null, null).getBody()).getEntry();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return savedFileNode;
  }

  @Override
  public List<FileDto> saveAttachmentsByIncomingDocId(List<MultipartFile> multipartFiles,
      String incomingDocId) {
    // create folder for incoming document attachments
    String folderId = folderService.createAttachmentFolderForIncomingDocument(
        incomingDocId);

    // upload files to folder
    List<FileDto> fileDtos = new ArrayList<>();
    for (MultipartFile multipartFile : multipartFiles) {
      Node file = uploadFile(multipartFile, folderId);
    }

    return fileDtos;
  }

  @Override
  public byte[] downloadFile(String fileId) {
    // Relevant when using API call from web browser, true is the default
    Boolean attachment = true;
    // Only download if modified since this time, optional
    OffsetDateTime ifModifiedSince = null;
    // The Range header indicates the part of a document that the server should return.
    // Single part request supported, for example: bytes=1-10., optional
    String range = null;

    Resource content = nodesApi.getNodeContent(fileId, attachment, ifModifiedSince, range)
        .getBody();
    try {
      byte[] bytes = content.getInputStream().readAllBytes();
      return bytes;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
