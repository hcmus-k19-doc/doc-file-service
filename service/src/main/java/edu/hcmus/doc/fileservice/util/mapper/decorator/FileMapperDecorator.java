package edu.hcmus.doc.fileservice.util.mapper.decorator;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import io.minio.GetObjectResponse;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import software.amazon.awssdk.services.s3.model.S3Object;

public abstract class FileMapperDecorator implements FileMapper {

  private static final String DATE_KEY = "Date";
  public static final String MIME_TYPE_KEY = "mime-type";

  public static final String CONTENT_TYPE_KEY = "Content-Type";

  public static final String CONTENT_LENGTH_KEY = "Content-Length";

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
    dto.setCreatedDate(file.headers().get(DATE_KEY));
    dto.setModifiedDate(file.headers().get(DATE_KEY));
    dto.setSize(Long.valueOf(Objects.requireNonNull(file.headers().get(CONTENT_LENGTH_KEY))));
    dto.setMimeType(file.headers().get(CONTENT_TYPE_KEY));
    return dto;
  }
}
