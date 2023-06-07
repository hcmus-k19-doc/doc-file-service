package edu.hcmus.doc.fileservice.util.mapper.decorator;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class FileMapperDecorator implements FileMapper {

  private static final String DATE_KEY = "Date";
  public static final String MIME_TYPE_KEY = "mime-type";

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

  @Override
  public FileDto toDto(String parentFolder, Long documentId, String fileName, GetObjectResponse file) {
    FileDto dto = delegate.toDto(parentFolder, documentId, fileName, file);
    dto.setCreatedDate(file.metadata().get(DATE_KEY));
    dto.setModifiedDate(file.lastModified().toString());
    dto.setSize(file.contentLength());
    dto.setMimeType(file.metadata().get(MIME_TYPE_KEY));
    return dto;
  }
}
