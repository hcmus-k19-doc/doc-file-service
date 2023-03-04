package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Definition;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.Property;
import org.alfresco.core.model.Site;
import org.alfresco.core.model.SiteBodyCreate;
import org.alfresco.core.model.SiteContainer;
import org.springframework.stereotype.Service;
import edu.hcmus.doc.fileservice.service.FileService;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  private final SitesApi sitesApi;
  private final NodesApi nodesApi;

  @Override
  public List<String> getFileTitles() {
    return Collections.emptyList();
  }



  @Override
  public Node uploadFile(FileDto fileDto) {
    Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(fileDto.getParentFolderId(), null, null,
        null).getBody()).getEntry();

    // Create the file node metadata
    Node fileNode = Objects.requireNonNull(nodesApi.createNode(parentFolderNode.getId(),
        new NodeBodyCreate().nodeType("cm:content").name(fileDto.getTitle()),
        null, null, null, null, null).getBody()).getEntry();

    // Add the file node content
    Node updatedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
        "Some text for this file...".getBytes(), true, null, null,
        null, null).getBody()).getEntry();

    return updatedFileNode;
  }


}
