package edu.hcmus.doc.fileservice.model.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileWrapper {

  @NotNull
  private byte[] data;
  @NotNull
  private String fileName;
  @NotNull
  private String contentType;
}
