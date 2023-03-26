package edu.hcmus.doc.fileservice.model.exception;

public class FileTypeNotAcceptedException extends RuntimeException {

  public static final String FILE_TYPE_NOT_ACCEPTED = "file.file_type_not_accepted";

  public FileTypeNotAcceptedException(String message) {
    super(message);
  }
}
