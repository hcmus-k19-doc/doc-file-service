package edu.hcmus.doc.fileservice.model.exception;

public class DocFileServiceRuntimeException extends RuntimeException {

  public static final String INTERNAL_SERVER_ERROR = "doc.exception.internal_server_error";

  public static final String FILE_TOO_LARGE = "file.file_too_large";

  public DocFileServiceRuntimeException(Throwable cause) {
    super(cause);
  }

  public DocFileServiceRuntimeException(String message) {
    super(message);
  }

  public DocFileServiceRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
