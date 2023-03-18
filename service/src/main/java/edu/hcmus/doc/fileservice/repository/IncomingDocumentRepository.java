package edu.hcmus.doc.fileservice.repository;

import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingDocumentRepository extends ElasticsearchRepository<IncomingDocument, String> {
  List<IncomingDocument> findByIncomingNumberOrOriginalSymbolNumberOrSummary(String incomingNumber, String originalSymbolNumber, String summary);

}
