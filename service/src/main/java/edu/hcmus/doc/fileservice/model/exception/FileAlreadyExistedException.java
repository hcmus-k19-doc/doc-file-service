package edu.hcmus.doc.fileservice.model.exception;

public class FileAlreadyExistedException extends ResourceAlreadyExistedException {

  public static final String FILE_ALREADY_EXISTED = "file.file_already_existed";

  public FileAlreadyExistedException(String message) {
    super(message);
  }
}
