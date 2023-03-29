package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.DocURL;
import edu.hcmus.doc.fileservice.model.dto.SiteDto;
import edu.hcmus.doc.fileservice.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.alfresco.core.model.Site;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/sites")
public class SiteController {
  private final SiteService siteService;

  @PostMapping
  public Site createSite(@RequestBody SiteDto siteDto) {
    return siteService.createSite(siteDto);
  }

  @GetMapping("/{siteId}")
  public Site getSiteBySiteId(@PathVariable String siteId) {
    return siteService.getSiteBySiteId(siteId);
  }

  @DeleteMapping("/{siteId}")
  public ResponseEntity<?> deleteSiteBySiteId(@PathVariable String siteId) {
    siteService.deleteSiteBySiteId(siteId);
    return ResponseEntity.ok().build();
  }
}
