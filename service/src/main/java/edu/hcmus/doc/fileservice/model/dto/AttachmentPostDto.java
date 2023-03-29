package edu.hcmus.doc.fileservice.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AttachmentPostDto {

  private List<FileWrapper> attachments;
  private Long incomingDocId;
}