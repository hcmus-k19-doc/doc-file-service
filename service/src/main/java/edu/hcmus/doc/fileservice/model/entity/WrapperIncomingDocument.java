package edu.hcmus.doc.fileservice.model.entity;

import java.util.Objects;

public class WrapperIncomingDocument {
  private final IncomingDocument incomingDocument;

  public WrapperIncomingDocument(IncomingDocument incomingDocument) {
    this.incomingDocument = incomingDocument;
  }

  public IncomingDocument unwrap() {
    return this.incomingDocument;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WrapperIncomingDocument that = (WrapperIncomingDocument) o;
    return Objects.equals(incomingDocument.getId(), that.incomingDocument.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(incomingDocument.getId());
  }
}
