package edu.hcmus.doc.fileservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DocFileServiceConfig {
  private final ObjectMapper objectMapper;

  @PostConstruct
  public void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
  }
}
