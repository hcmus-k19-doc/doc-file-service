package edu.hcmus.doc.fileservice.model.exception;

public abstract class ResourceNotFoundException extends RuntimeException {

  protected ResourceNotFoundException(String message) {
    super(message);
  }
}
