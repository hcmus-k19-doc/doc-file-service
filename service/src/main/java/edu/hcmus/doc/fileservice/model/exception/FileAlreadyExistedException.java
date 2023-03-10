package edu.hcmus.doc.fileservice.model.exception;

public class FileAlreadyExistedException extends ResourceAlreadyExistedException {

  public static final String FILE_ALREADY_EXISTED = "FILE.ALREADY_EXISTED";

  public FileAlreadyExistedException(String message) {
    super(message);
  }
}
