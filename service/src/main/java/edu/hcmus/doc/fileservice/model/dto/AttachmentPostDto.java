package edu.hcmus.doc.fileservice.model.dto;

import java.util.List;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AttachmentPostDto {
  private List<MultipartFile> attachments;
  private Long incomingDocId;
}
