package edu.hcmus.doc.fileservice.model.dto;

import lombok.Data;

@Data
public class FileWrapper {

  private byte[] data;
  private String fileName;
  private String contentType;
}
