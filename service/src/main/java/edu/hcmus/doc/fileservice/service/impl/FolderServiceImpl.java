package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import edu.hcmus.doc.fileservice.service.FolderService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Definition;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeChildAssociationPagingList;
import org.alfresco.core.model.Property;
import org.alfresco.core.model.SiteContainer;
import org.alfresco.search.handler.SearchApi;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FolderServiceImpl implements FolderService {

  private final SitesApi sitesApi;

  private final NodesApi nodesApi;

  private final SearchApi searchApi;

  @Override
  public Node createFolder(FolderDto folderDto) {
    // create site if not exist


    SiteContainer docLibContainer = Objects.requireNonNull(
        sitesApi.getSiteContainer(folderDto.getSiteId(),
            "documentLibrary", null).getBody()).getEntry();

    if (docLibContainer == null) {
      // create site
      System.out.println("create site");
      // create doc lib
    }

    Property property = new Property();
    property.setTitle(folderDto.getTitle());
    property.setDescription(folderDto.getDescription());

    Definition definition = new Definition();
    definition.setProperties(Collections.singletonList(property));

    Node folderNode = Objects.requireNonNull(nodesApi.createNode(docLibContainer.getId(),
        new NodeBodyCreate()
            .nodeType("cm:folder")
            .name(folderDto.getTitle())
            .definition(definition),
        null, null, null, null, null).getBody()).getEntry();

    return folderNode;
  }

  @Override
  public Node getFolderById(String folderId) {
    return Objects.requireNonNull(nodesApi.getNode(folderId, null, null, null).getBody())
        .getEntry();
  }

  @Override
  public List<Node> getFoldersBySiteId(String siteId) {
    SiteContainer docLibContainer = Objects.requireNonNull(
        sitesApi.getSiteContainer(siteId,
            "documentLibrary", null).getBody()).getEntry();

    return null;
  }

  @Override
  public NodeChildAssociationPagingList getFolderContent(String folderId) {
    Integer skipCount = 0;
    Integer maxItems = 100;
    List<String> include = null;
    List<String> fields = null;
    List<String> orderBy = null;
    String where = null;
    Boolean includeSource = false;

    NodeChildAssociationPagingList result = nodesApi.listNodeChildren(folderId, skipCount, maxItems, orderBy, where, include,null, includeSource, fields).getBody().getList();
    return result;
  }
}
