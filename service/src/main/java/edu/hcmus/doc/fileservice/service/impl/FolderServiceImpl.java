package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import edu.hcmus.doc.fileservice.service.FolderService;
import java.util.Collections;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Definition;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.Property;
import org.alfresco.core.model.SiteContainer;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

  private final SitesApi sitesApi;

  private final NodesApi nodesApi;

  @Override
  public Node createFolder(FolderDto folderDto) {
    SiteContainer docLibContainer = Objects.requireNonNull(
        sitesApi.getSiteContainer(folderDto.getSiteId(),
            "documentLibrary", null).getBody()).getEntry();

    Property property = new Property();
    property.setTitle(folderDto.getTitle());
    property.setDescription(folderDto.getDescription());

    Definition definition = new Definition();
    definition.setProperties(Collections.singletonList(property));

    Node folderNode = Objects.requireNonNull(nodesApi.createNode(docLibContainer.getId(),
        new NodeBodyCreate()
            .nodeType("cm:folder")
            .name(folderDto.getTitle()),
        null, null, null, null, null).getBody()).getEntry();

    return folderNode;
  }

  @Override
  public Node getFolderById(String folderId) {
    return Objects.requireNonNull(nodesApi.getNode(folderId, null, null, null).getBody())
        .getEntry();
  }
}
