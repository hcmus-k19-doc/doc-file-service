package edu.hcmus.doc.fileservice.model.exception;

public class FolderNotFoundException extends ResourceNotFoundException {

  public static final String FOLDER_NOT_FOUND = "folder.folder_not_found";

  public FolderNotFoundException(String message) {
    super(message);
  }
}
