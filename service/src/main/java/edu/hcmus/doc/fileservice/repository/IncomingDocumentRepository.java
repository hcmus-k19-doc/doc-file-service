package edu.hcmus.doc.fileservice.repository;

import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomingDocumentRepository extends ElasticsearchRepository<IncomingDocument, String> {
  List<IncomingDocument> findByIncomingNumber(String incomingNumber);

  List<IncomingDocument> findByOriginalSymbolNumber(String originalSymbolNumber);

  List<IncomingDocument> findBySummary(String summary);

  List<IncomingDocument> findByIncomingNumberAndOriginalSymbolNumber(String incomingNumber, String originalSymbolNumber);

  List<IncomingDocument> findByIncomingNumberAndSummary(String incomingNumber, String summary);

  List<IncomingDocument> findByOriginalSymbolNumberAndSummary(String originalSymbolNumber, String summary);

  List<IncomingDocument> findByIncomingNumberAndOriginalSymbolNumberAndSummary(String incomingNumber, String originalSymbolNumber, String summary);
}
