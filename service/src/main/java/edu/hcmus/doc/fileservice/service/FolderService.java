package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import org.alfresco.core.model.Node;

public interface FolderService {
  // get folder by id
  Node getFolderById(String folderId);

  Node createFolder(FolderDto folderDto);
}
