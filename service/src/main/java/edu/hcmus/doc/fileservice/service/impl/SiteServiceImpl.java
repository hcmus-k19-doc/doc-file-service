package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import edu.hcmus.doc.fileservice.model.exception.SiteAlreadyExistedException;
import edu.hcmus.doc.fileservice.model.exception.SiteNotFoundException;
import edu.hcmus.doc.fileservice.service.SiteService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
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
    if (getSiteBySiteId(siteDto.getId()) != null) {
      throw new SiteAlreadyExistedException(SiteAlreadyExistedException.SITE_ALREADY_EXISTED);
    }

    Site site = Objects.requireNonNull(sitesApi.createSite(
        new SiteBodyCreate()
            .id(siteDto.getId())
            .title(siteDto.getTitle())
            .description(siteDto.getDescription())
            .visibility(siteDto.getVisibility()),
        null, null, null).getBody()).getEntry();
    return site;
  }

  @Override
  public Site getSiteBySiteId(String siteId) {
    // Check if a site exists
    try {
      Site site = sitesApi.getSite(siteId, null, null).getBody().getEntry();
      System.out.println("Site exists: " + siteId);
      return site;
    } catch (Exception e) {
      System.out.println("Site does not exist: " + siteId);
      throw new SiteNotFoundException(SiteNotFoundException.SITE_NOT_FOUND);
    }
  }

  @Override
  public void deleteSiteBySiteId(String siteId) {
    if (getSiteBySiteId(siteId) != null) {
      sitesApi.deleteSite(siteId, null);
    }
  }
}
