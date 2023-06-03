package edu.hcmus.doc.fileservice.model.exception;

public class DocFileServiceRuntimeException extends RuntimeException {

  public DocFileServiceRuntimeException(Throwable cause) {
    super(cause);
  }

  public DocFileServiceRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
