package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.model.dto.ExceptionDto;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNoContentException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceNotFoundException;
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
        .status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(ResourceAlreadyExistedException.class)
  public ResponseEntity<ExceptionDto> handleResourceAlreadyExistedException(
      ResourceAlreadyExistedException exception) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ExceptionDto> handleResourceNotFoundException(
      ResourceNotFoundException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(AttachmentNoContentException.class)
  public ResponseEntity<ExceptionDto> handleAttachmentNoContentException(
      AttachmentNoContentException exception) {
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ExceptionDto> handleMaxSizeException() {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ExceptionDto("file.file_too_large"));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ExceptionDto> handleInternalErrorException(Throwable throwable) {
    return ResponseEntity
        .internalServerError()
        .body(new ExceptionDto("INTERNAL_SERVER_ERROR"));
  }
}
