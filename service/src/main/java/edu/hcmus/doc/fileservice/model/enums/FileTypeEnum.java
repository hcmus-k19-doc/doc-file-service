package edu.hcmus.doc.fileservice.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileTypeEnum {
  PDF("pdf"),
  JPG("jpg"),
  JPEG("jpeg"),
  PNG("png");

  public final String value;
}
