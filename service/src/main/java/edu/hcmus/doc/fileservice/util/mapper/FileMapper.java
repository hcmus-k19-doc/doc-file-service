package edu.hcmus.doc.fileservice.util.mapper;

import edu.hcmus.doc.fileservice.model.dto.FileDto;
import java.time.OffsetDateTime;
import org.alfresco.core.model.Node;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
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

  @Named("offsetDateTimeToString")
  default String offsetDateTimeToString(OffsetDateTime offsetDateTime) {
    return offsetDateTime.toString();
  }
}
