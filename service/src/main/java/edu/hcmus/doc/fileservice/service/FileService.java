package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.Site;

import java.util.List;

public interface FileService {

  List<String> getFileTitles();




   Node uploadFile(FileDto fileDto);


}
