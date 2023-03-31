package edu.hcmus.doc.fileservice.model.exception;

public class SiteNotFoundException extends ResourceNotFoundException {

  public static final String SITE_NOT_FOUND = "site.site_not_found";

  public SiteNotFoundException(String message) {
    super(message);
  }
}
