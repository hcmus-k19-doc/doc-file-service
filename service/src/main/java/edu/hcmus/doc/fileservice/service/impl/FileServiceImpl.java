package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.service.FileService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
  public Node uploadFile(MultipartFile multipartFile, String parentFolderId) {
    Node parentFolderNode = Objects.requireNonNull(nodesApi.getNode(parentFolderId, null, null,
        null).getBody()).getEntry();

    // Create the file node metadata
    Node fileNode = Objects.requireNonNull(nodesApi.createNode(parentFolderNode.getId(),
        new NodeBodyCreate().nodeType("cm:content").name(multipartFile.getOriginalFilename()),
        null, null, null, null, null).getBody()).getEntry();

    // Add the file node content
    Node updatedFileNode = null;
    try {
      updatedFileNode = Objects.requireNonNull(nodesApi.updateNodeContent(fileNode.getId(),
          multipartFile.getBytes(), true, null, null,
          null, null).getBody()).getEntry();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return updatedFileNode;
  }
}
