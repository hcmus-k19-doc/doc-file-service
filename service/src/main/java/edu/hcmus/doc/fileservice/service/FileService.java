package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentDto;
import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import java.util.List;
import org.alfresco.core.model.Node;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  Boolean isValidFile(String fileName, String fileExtension, String parentFolderId);

  Boolean isFileExist(String fileName, String parentFolderId);

  List<String> getFileTitles();

  Node uploadFile(MultipartFile multipartFile, String parentFolderId);

  Node uploadFilev2(FileWrapper fileWrapper, String parentFolderId);

  List<FileDto> saveAttachmentsByIncomingDocId(AttachmentPostDto attachmentPostDto);

  FileDto downloadFile(AttachmentDto attachmentDto);
}
