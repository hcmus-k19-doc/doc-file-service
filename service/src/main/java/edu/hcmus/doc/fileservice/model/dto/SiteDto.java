package edu.hcmus.doc.fileservice.model.dto;

import lombok.Data;
import org.alfresco.core.model.SiteBodyCreate.VisibilityEnum;

@Data
public class SiteDto {

  private String title;
  private String id;
  private VisibilityEnum visibility;
  private String description;
}
