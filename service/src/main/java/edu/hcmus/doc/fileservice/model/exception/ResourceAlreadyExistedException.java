package edu.hcmus.doc.fileservice.model.exception;

public abstract class ResourceAlreadyExistedException extends RuntimeException {

  protected ResourceAlreadyExistedException(String message) {
    super(message);
  }
}
