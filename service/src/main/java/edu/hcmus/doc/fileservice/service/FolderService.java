package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import java.util.List;
import org.alfresco.core.model.Node;

public interface FolderService {
  // get folder by id
  Node getFolderById(String folderId);

  List<Node> getFoldersBySiteId(String siteId);

  Node createFolder(FolderDto folderDto);
}
