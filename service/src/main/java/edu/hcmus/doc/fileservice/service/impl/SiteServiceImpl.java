package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import edu.hcmus.doc.fileservice.service.SiteService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.handler.SitesApi;
import org.alfresco.core.model.Site;
import org.alfresco.core.model.SiteBodyCreate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {

  private final SitesApi sitesApi;

  @Override
  public Site createSite(SiteDto siteDto) {
    Site site = Objects.requireNonNull(sitesApi.createSite(
        new SiteBodyCreate()
            .id(siteDto.getId())
            .title(siteDto.getTitle())
            .description(siteDto.getDescription())
            .visibility(siteDto.getVisibility()),
        null, null, null).getBody()).getEntry();
    return site;
  }
}