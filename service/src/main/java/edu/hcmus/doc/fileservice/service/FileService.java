package edu.hcmus.doc.fileservice.service;

import java.util.List;
import org.alfresco.core.model.Node;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  Boolean isFileExist(String fileName, String parentFolderId);

  List<String> getFileTitles();

  Node uploadFile(MultipartFile multipartFile, String parentFolderId);

  byte[] downloadFile(String fileId);
}
