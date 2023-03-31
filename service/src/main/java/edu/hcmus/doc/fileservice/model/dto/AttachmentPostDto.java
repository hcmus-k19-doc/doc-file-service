package edu.hcmus.doc.fileservice.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class AttachmentPostDto {

  private List<FileWrapper> attachments;
  private Long incomingDocId;
}