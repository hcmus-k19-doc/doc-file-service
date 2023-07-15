package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.DocFileServiceURL;
import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import edu.hcmus.doc.fileservice.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocFileServiceURL.API_V1 + "/folders")
public class FolderController {

  private final FolderService folderService;

  @GetMapping("/{folderId}")
  public Node getFolderById(@PathVariable String folderId) {
    return folderService.getFolderByFolderId(folderId);
  }

  @GetMapping("/content/{folderId}")
  public ResponseEntity<NodeChildAssociationPagingList> getFolderContent(@PathVariable String folderId) {
    return ResponseEntity.ok(folderService.getFolderContent(folderId));
  }

  @PostMapping
  public Node createFolder(@RequestBody FolderDto folderDto) {
    return folderService.createFolder(folderDto);
  }
}
