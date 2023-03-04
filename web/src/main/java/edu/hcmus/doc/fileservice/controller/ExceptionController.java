package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.model.dto.ExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ExceptionDto> handleMaxSizeException(MaxUploadSizeExceededException exception) {
    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ExceptionDto("FILE_TOO_LARGE"));
  }
}
