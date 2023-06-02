package edu.hcmus.doc.fileservice.service.impl;

import static edu.hcmus.doc.fileservice.model.enums.FileType.ALLOWED_FILE_TYPES;

import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentDto;
import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNoContentException;
import edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException;
import edu.hcmus.doc.fileservice.model.exception.FileAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.service.FileService;
import edu.hcmus.doc.fileservice.service.FolderService;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeChildAssociationEntry;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = Throwable.class)
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
    return ALLOWED_FILE_TYPES.contains(fileType);
  }

  @Override
  public boolean isFileExist(String fileName, String parentFolderId) {
    NodeChildAssociationPagingList folderContent = folderService.getFolderContent(parentFolderId);
    if (!folderContent.getEntries().isEmpty()) {
      for (NodeChildAssociationEntry nodeEntry : folderContent.getEntries()) {
        if (nodeEntry.getEntry().getName().equals(fileName)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Node uploadFile(MultipartFile multipartFile, String parentFolderId) throws IOException {
    // check if file already exists
    if (isFileExist(multipartFile.getOriginalFilename(), parentFolderId)) {
      throw new FileAlreadyExistedException(FileAlreadyExistedException.FILE_ALREADY_EXISTED);
    }

    // check if file type is allowed
    if (!CollectionUtils.containsAny(ALLOWED_FILE_TYPES, multipartFile.getContentType())) {
      throw new FileTypeNotAcceptedException(FileTypeNotAcceptedException.FILE_TYPE_NOT_ACCEPTED);
    }

    Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(parentFolderId, null, null,
        null).getBody()).getEntry();

    // Create the file node metadata
    Node fileNode = Optional.ofNullable(
            nodesApi.createNode(
                    parentFolderNode.getId(),
                    new NodeBodyCreate().nodeType("cm:content")
                        .name(multipartFile.getOriginalFilename()),
                    null,
                    null,
                    null,
                    null,
                    null)
                .getBody())
        .orElseThrow()
        .getEntry();

    // Add the file node content
    return Optional.ofNullable(
            nodesApi.updateNodeContent(
                fileNode.getId(),
                multipartFile.getBytes(),
                true,
                null,
                null,
                null,
                null).getBody())
        .orElseThrow()
        .getEntry();
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
    Node savedFileNode;
    savedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
        fileWrapper.getData(), true, null, null,
        null, null).getBody()).getEntry();

    return savedFileNode;
  }

  @Override
  @RabbitListener(queues = "${spring.rabbitmq.template.attachment-queue}")
  public List<FileDto> saveAttachmentsByIncomingDocId(AttachmentPostDto attachmentPostDto) {
    // get the routing key

    log.info("Go to saveAttachmentsByIncomingDocId");
    log.info("Received request from main service");
    log.info("IncomingDocId: {}", attachmentPostDto.getDocId());
    // create folder for incoming document attachments
    String folderId = folderService.createAttachmentFolderForIncomingDocument(
        attachmentPostDto.getDocId());

    // upload files to folder
    List<FileDto> fileDtos = new ArrayList<>();
    for (FileWrapper fileWrapper : attachmentPostDto.getAttachments()) {
      Node file = uploadFilev2(fileWrapper, folderId);
      fileDtos.add(fileMapper.toDto(file));
    }

    return fileDtos;
  }

  @Override
  public FileDto downloadFile(AttachmentDto attachmentDto) {
    Boolean attachment = true;

    Resource content = Optional.ofNullable(nodesApi.getNodeContent(
        attachmentDto.getAlfrescoFileId(), attachment,
        null, null).getBody()).orElseThrow();
    try {
      return FileDto.builder()
          .data(content.getInputStream().readAllBytes())
          .title(content.getFilename())
          .mimeType(attachmentDto.getFileType().value)
          .build();
    } catch (IOException e) {
      throw new DocFileServiceRuntimeException(e);
    }
  }

  @SneakyThrows
  @Override
  public byte[] downloadIncomingDocFolder(List<AttachmentDto> attachmentDtoList) {
    if (attachmentDtoList.isEmpty()) {
      throw new AttachmentNoContentException(AttachmentNoContentException.ATTACHMENT_NO_CONTENT);
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

    attachmentDtoList.forEach(attachmentDto -> {
      FileDto fileDto = downloadFile(attachmentDto);
      try {
        ZipEntry zipEntry = new ZipEntry(fileDto.getTitle());
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(fileDto.getData());

        // Close the zip entry
        zipOutputStream.closeEntry();
      } catch (IOException e) {
        throw new DocFileServiceRuntimeException(e);
      }
    });

    zipOutputStream.close();
    return byteArrayOutputStream.toByteArray();
  }
}
