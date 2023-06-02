package edu.hcmus.doc.fileservice.util.mapper.decorator;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class FileMapperDecorator implements FileMapper {

  @Autowired
  @Qualifier("delegate")
  private FileMapper delegate;

  @Override
  public FileDto toDto(S3Object file) {
    FileDto dto = delegate.toDto(file);
    dto.setTitle(file.key());
    dto.setSize(file.size());
    dto.setModifiedDate(file.lastModified().toString());
    return dto;
  }
}
