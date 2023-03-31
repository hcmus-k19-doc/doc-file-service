package edu.hcmus.doc.fileservice.model.exception;

public class SiteAlreadyExistedException extends ResourceAlreadyExistedException {

  public static final String SITE_ALREADY_EXISTED = "site.site_already_existed";

  public SiteAlreadyExistedException(String message) {
    super(message);
  }
}
