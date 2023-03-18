package edu.hcmus.doc.fileservice.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import edu.hcmus.doc.fileservice.model.dto.IncomingDocumentCriteriaDto;
import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import edu.hcmus.doc.fileservice.repository.IncomingDocumentRepository;
import edu.hcmus.doc.fileservice.service.IncomingDocumentService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomingDocumentServiceImpl implements IncomingDocumentService {
  private final IncomingDocumentRepository incomingDocumentRepository;

  private final ElasticsearchOperations operations;


  @Override
  public List<IncomingDocument> getIncomingDocuments(IncomingDocumentCriteriaDto incomingDocumentCriteriaDto) {
//    var query = new NativeSearchQueryBuilder()
//        .withQuery(queryStringQuery("incoming_number:" + incomingDocumentCriteriaDto.getIncomingNumber()))
//        .build();
//
//
//    SearchHits<IncomingDocument> incomingDocumentHits = operations.search(query, IncomingDocument.class, IndexCoordinates.of("logstash-incoming-document-index"));
//
//    List<IncomingDocument> incomingDocumentMatches = new ArrayList<>();
//    incomingDocumentHits.forEach(searchHit->{
//      incomingDocumentMatches.add(searchHit.getContent());
//    });
//    return incomingDocumentMatches;
    return incomingDocumentRepository.findByIncomingNumberOrOriginalSymbolNumberOrSummary(incomingDocumentCriteriaDto.getIncomingNumber(), incomingDocumentCriteriaDto.getOriginalSymbolNumber(), incomingDocumentCriteriaDto.getSummary());
  }



}
