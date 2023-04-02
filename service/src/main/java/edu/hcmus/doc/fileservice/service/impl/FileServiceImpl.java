package edu.hcmus.doc.fileservice.service.impl;

import static edu.hcmus.doc.fileservice.common.Constants.ALLOWED_FILE_TYPES;

import edu.hcmus.doc.fileservice.model.dto.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.exception.FileAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.service.FileService;
import edu.hcmus.doc.fileservice.service.FolderService;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeChildAssociationEntry;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  private final NodesApi nodesApi;

  private final FolderService folderService;

  private final FileMapper fileMapper;

  @Override
  public List<String> getFileTitles() {
    return Collections.emptyList();
  }

  @Override
  public Boolean isValidFile(String fileName, String fileType, String parentFolderId) {
    // check if file already exists
    if (isFileExist(fileName, parentFolderId)) {
      throw new FileAlreadyExistedException(FileAlreadyExistedException.FILE_ALREADY_EXISTED);
    }
    // check if file type is allowed
    if (ALLOWED_FILE_TYPES.contains(fileType)) {
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
    if (!ALLOWED_FILE_TYPES.contains(multipartFile.getContentType().toLowerCase())) {
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
  public Node uploadFilev2(FileWrapper fileWrapper, String parentFolderId) {
    // check if file already exists
    if (isFileExist(fileWrapper.getFileName(), parentFolderId)) {
      throw new FileAlreadyExistedException(FileAlreadyExistedException.FILE_ALREADY_EXISTED);
    }

    // check if file type is allowed
    if (!ALLOWED_FILE_TYPES.contains(fileWrapper.getContentType().toLowerCase())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(parentFolderId, null, null,
        null).getBody()).getEntry();

    // Create the file node metadata
    Node fileNode = Objects.requireNonNull(nodesApi.createNode(parentFolderNode.getId(),
        new NodeBodyCreate().nodeType("cm:content").name(fileWrapper.getFileName()),
        null, null, null, null, null).getBody()).getEntry();

    // Add the file node content
    Node savedFileNode = null;
    savedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
        fileWrapper.getData(), true, null, null,
        null, null).getBody()).getEntry();

    return savedFileNode;
  }

  @Override
  @RabbitListener(queues = "${spring.rabbitmq.template.attachment-queue}")
  public List<FileDto> saveAttachmentsByIncomingDocId(AttachmentPostDto attachmentPostDto) {
    // get the routing key

    System.out.println("Go to saveAttachmentsByIncomingDocId");
    System.out.println("Received request from main service");
    System.out.println("IncomingDocId: " + attachmentPostDto.getIncomingDocId());
    // create folder for incoming document attachments
    String folderId = folderService.createAttachmentFolderForIncomingDocument(
        attachmentPostDto.getIncomingDocId());

    // upload files to folder
    List<FileDto> fileDtos = new ArrayList<>();
    for (FileWrapper fileWrapper : attachmentPostDto.getAttachments()) {
      Node file = uploadFilev2(fileWrapper, folderId);
      fileDtos.add(fileMapper.toDto(file));
    }

    return fileDtos;
  }

  @Override
  public byte[] downloadFile(String fileId) {
    Boolean attachment = true;
    OffsetDateTime ifModifiedSince = null;
    String range = null;

    Resource content = nodesApi.getNodeContent(fileId, attachment, ifModifiedSince, range)
        .getBody();
    try {
      return content.getInputStream().readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
