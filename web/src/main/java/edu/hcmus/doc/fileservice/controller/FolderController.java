package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.DocURL;
import edu.hcmus.doc.fileservice.model.dto.FolderDto;
import edu.hcmus.doc.fileservice.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/folders")
public class FolderController {

  private final FolderService folderService;

  @PostMapping
  public String createFolder(@RequestBody FolderDto folderDto) {
    return folderService.createFolder(folderDto);
  }
}
