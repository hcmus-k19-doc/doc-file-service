package edu.hcmus.doc.fileservice.controller;

import static edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException.FILE_TOO_LARGE;
import static edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException.INTERNAL_SERVER_ERROR;

import edu.hcmus.doc.fileservice.model.dto.ExceptionDto;
import edu.hcmus.doc.fileservice.model.exception.AttachmentNoContentException;
import edu.hcmus.doc.fileservice.model.exception.FileTypeNotAcceptedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.ResourceNotFoundException;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
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
        .body(new ExceptionDto(FILE_TOO_LARGE));
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ExceptionDto> handleMinioErrorResponseException(ErrorResponseException exception) {
    log.error(exception.getMessage(), exception);
    if ("NoSuchKey".equals(exception.errorResponse().code())) {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body(new ExceptionDto("Attachment not found"));
    }

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ExceptionDto(exception.getMessage()));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ExceptionDto> handleInternalErrorException(Throwable throwable) {
    log.error(throwable.getMessage(), throwable);
    return ResponseEntity
        .internalServerError()
        .body(new ExceptionDto(INTERNAL_SERVER_ERROR));
  }
}
