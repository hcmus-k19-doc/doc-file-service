package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import java.util.List;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeChildAssociationPagingList;

public interface FolderService {
  Node getFolderById(String folderId);

  List<Node> getFoldersBySiteId(String siteId);

  NodeChildAssociationPagingList getFolderContent(String folderId);

  Node createFolder(FolderDto folderDto);
}
