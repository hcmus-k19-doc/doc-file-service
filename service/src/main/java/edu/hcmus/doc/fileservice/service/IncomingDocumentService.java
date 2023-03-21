package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.IncomingDocumentCriteriaDto;
import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import java.util.List;

public interface IncomingDocumentService {
  List<IncomingDocument> getIncomingDocuments(IncomingDocumentCriteriaDto incomingDocumentCriteriaDto);
}
