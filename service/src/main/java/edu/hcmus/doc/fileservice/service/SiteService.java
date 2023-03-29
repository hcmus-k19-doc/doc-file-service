package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import org.alfresco.core.model.Site;

public interface SiteService {

  Site createSite(SiteDto siteDto);

  Site getSiteBySiteId(String siteId);

  void deleteSiteBySiteId(String siteId);
}
