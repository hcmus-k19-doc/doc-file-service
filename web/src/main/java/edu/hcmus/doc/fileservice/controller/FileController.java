package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.DocURL;
import edu.hcmus.doc.fileservice.model.dto.Attachment.AttachmentDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.model.Node;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/files")
public class FileController {

  private final FileService fileService;

  @GetMapping
  public List<String> getFiles() {
    return fileService.getFileTitles();
  }

  @PostMapping
  public Node uploadFile(@RequestParam("file") MultipartFile multipartFile,
      @RequestParam("parentFolderId") String parentFolderId) {
    return fileService.uploadFile(multipartFile, parentFolderId);
  }

  @PostMapping("/download")
  public ResponseEntity<?> downloadFile(@RequestBody AttachmentDto attachmentDto) {
    FileDto fileDto = fileService.downloadFile(attachmentDto);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf(fileDto.getMimeType()))
        .body(fileDto.getData());
  }

}
