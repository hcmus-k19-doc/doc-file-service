package edu.hcmus.doc.fileservice.model.exception;

public class AttachmentNoContentException extends RuntimeException {
  public static final String ATTACHMENT_NO_CONTENT = "attachment.no_content";

  public AttachmentNoContentException(String message) {
    super(message);
  }
}
