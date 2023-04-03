package edu.hcmus.doc.fileservice.model.dto.Attachment;

import edu.hcmus.doc.fileservice.model.enums.FileType;
import lombok.Data;

@Data
public class AttachmentDto {

  private Long incomingDocId;
  private String alfrescoFileId;
  private String alfrescoFolderId;
  private FileType fileType;
}
