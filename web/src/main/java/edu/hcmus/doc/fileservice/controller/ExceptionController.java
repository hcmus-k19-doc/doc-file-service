package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.model.dto.ExceptionDto;
import edu.hcmus.doc.fileservice.model.exception.FileAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceAlreadyExistedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(FileTypeNotAcceptedException.class)
  public ResponseEntity<ExceptionDto> handleFileTypeNotAcceptedException(
      FileTypeNotAcceptedException exception) {
    return ResponseEntity
        .status(HttpStatus.EXPECTATION_FAILED)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(ResourceAlreadyExistedException.class)
  public ResponseEntity<ExceptionDto> handleResourceAlreadyExistedException(
      ResourceAlreadyExistedException exception) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ExceptionDto> handleMaxSizeException(
      MaxUploadSizeExceededException exception) {
    return ResponseEntity
        .status(HttpStatus.EXPECTATION_FAILED)
        .body(new ExceptionDto("FILE.FILE_TOO_LARGE"));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ExceptionDto> handleInternalErrorException(Throwable throwable) {
    return ResponseEntity
        .internalServerError()
        .body(new ExceptionDto("INTERNAL_SERVER_ERROR"));
  }
}
