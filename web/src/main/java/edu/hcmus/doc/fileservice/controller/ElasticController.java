package edu.hcmus.doc.fileservice.controller;

import edu.hcmus.doc.fileservice.model.dto.IncomingDocumentCriteriaDto;
import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import edu.hcmus.doc.fileservice.service.IncomingDocumentService;
import edu.hcmus.doc.fileservice.DocURL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/elastic/incoming-document")
public class ElasticController {
  private final IncomingDocumentService incomingDocumentService;

  @PostMapping
  public List<IncomingDocument> getIncomingDocuments(@RequestBody IncomingDocumentCriteriaDto incomingDocumentCriteriaDto) {
    return incomingDocumentService.getIncomingDocuments(incomingDocumentCriteriaDto);
  }



}
