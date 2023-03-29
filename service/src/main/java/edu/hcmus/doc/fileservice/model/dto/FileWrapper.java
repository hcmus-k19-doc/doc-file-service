package edu.hcmus.doc.fileservice.model.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class FileWrapper {

  private byte[] data;
  private String fileName;
  private String contentType;
}
