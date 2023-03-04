package edu.hcmus.doc.fileservice.model.enums;

public enum SiteVisibility {
  PUBLIC("PUBLIC"),
  PRIVATE("PRIVATE"),
  MODERATED("MODERATED");
  private final String value;

  SiteVisibility(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
