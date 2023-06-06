package edu.hcmus.doc.fileservice.util.mapper;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.util.mapper.decorator.FileMapperDecorator;
import java.time.OffsetDateTime;
import org.alfresco.core.model.Node;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Mapper(componentModel = "spring")
@DecoratedWith(FileMapperDecorator.class)
public interface FileMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "parentId", target = "parentFolderId")
  @Mapping(source = "name", target = "title")
  @Mapping(source = "modifiedAt", target = "modifiedDate", qualifiedByName = "offsetDateTimeToString")
  @Mapping(source = "modifiedByUser.displayName", target = "modifier")
  @Mapping(source = "createdAt", target = "createdDate", qualifiedByName = "offsetDateTimeToString")
  @Mapping(source = "createdByUser.displayName", target = "creator")
  @Mapping(source = "content.sizeInBytes", target = "size")
  @Mapping(source = "content.mimeType", target = "mimeType")
  @Mapping(target = "url", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "data", ignore = true)
  FileDto toDto(Node file);

  FileDto toDto(S3Object file);

  @Mapping(target = "url", ignore = true)
  @Mapping(target = "size", ignore = true)
  @Mapping(target = "modifier", ignore = true)
  @Mapping(target = "modifiedDate", ignore = true)
  @Mapping(target = "mimeType", ignore = true)
  @Mapping(target = "description", ignore = true)
  @Mapping(target = "data", ignore = true)
  @Mapping(target = "creator", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "parentFolderId", expression = "java(parentFolder + \"/\" + documentId)")
  @Mapping(target = "id", expression = "java(parentFolder + \"/\" + documentId + \"/\" + fileName)")
  @Mapping(target = "title", source = "fileName")
  FileDto toDto(String parentFolder, Long documentId, String fileName, GetObjectResponse file);

  @Named("offsetDateTimeToString")
  default String offsetDateTimeToString(OffsetDateTime offsetDateTime) {
    return offsetDateTime.toString();
  }
}
