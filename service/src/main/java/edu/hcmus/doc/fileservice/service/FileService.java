package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentDto;
import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import java.io.IOException;
import java.util.List;
import org.alfresco.core.model.Node;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  Boolean isValidFile(String fileName, String fileExtension, String parentFolderId);

  boolean isFileExist(String fileName, String parentFolderId);

  List<String> getFileTitles();

  Node uploadFile(MultipartFile multipartFile, String parentFolderId) throws IOException;

  Node uploadFilev2(FileWrapper fileWrapper, String parentFolderId);

  List<FileDto> saveAttachmentsByIncomingDocId(AttachmentPostDto attachmentPostDto);

  FileDto downloadFile(AttachmentDto attachmentDto);

  byte[] downloadIncomingDocFolder(List<AttachmentDto> attachmentDtoList);

}
