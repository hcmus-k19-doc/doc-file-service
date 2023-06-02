package edu.hcmus.doc.fileservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DocFileServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocFileServiceApplication.class, args);
    log.info("DOC File Service Application is running");
  }
}
