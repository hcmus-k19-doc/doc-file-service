package edu.hcmus.doc.fileservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDto {

  private String id;
  private String parentFolderId;
  private String title;
  private String description;
  private String createdDate;
  private String modifiedDate;
  private String creator;
  private String modifier;
  private Double size;
  private String mimeType;
  private String url;
  private byte[] data;
}
