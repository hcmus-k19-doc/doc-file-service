package edu.hcmus.doc.fileservice.service.impl;

import edu.hcmus.doc.fileservice.model.dto.IncomingDocumentCriteriaDto;
import edu.hcmus.doc.fileservice.model.entity.IncomingDocument;
import edu.hcmus.doc.fileservice.repository.IncomingDocumentRepository;
import edu.hcmus.doc.fileservice.service.IncomingDocumentService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomingDocumentServiceImpl implements IncomingDocumentService {
  private final IncomingDocumentRepository incomingDocumentRepository;

  @Override
  public List<IncomingDocument> getIncomingDocuments(IncomingDocumentCriteriaDto incomingDocumentCriteriaDto) {
    if(incomingDocumentCriteriaDto != null) {
      if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getIncomingNumber()) && StringUtils.isNotBlank(incomingDocumentCriteriaDto.getOriginalSymbolNumber()) && StringUtils.isNotBlank(incomingDocumentCriteriaDto.getSummary())) {
        return incomingDocumentRepository.findByIncomingNumberAndOriginalSymbolNumberAndSummary(incomingDocumentCriteriaDto.getIncomingNumber(), incomingDocumentCriteriaDto.getOriginalSymbolNumber(), incomingDocumentCriteriaDto.getSummary());
      } else {
        if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getIncomingNumber()) && StringUtils.isNotBlank(incomingDocumentCriteriaDto.getOriginalSymbolNumber())) {
          return incomingDocumentRepository.findByIncomingNumberAndOriginalSymbolNumber(incomingDocumentCriteriaDto.getIncomingNumber(), incomingDocumentCriteriaDto.getOriginalSymbolNumber());
        } else {
          if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getIncomingNumber()) && StringUtils.isNotBlank(incomingDocumentCriteriaDto.getSummary())) {
            return incomingDocumentRepository.findByIncomingNumberAndSummary(incomingDocumentCriteriaDto.getIncomingNumber(), incomingDocumentCriteriaDto.getSummary());
          } else {
            if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getOriginalSymbolNumber()) && StringUtils.isNotBlank(incomingDocumentCriteriaDto.getSummary())) {
              return incomingDocumentRepository.findByOriginalSymbolNumberAndSummary(incomingDocumentCriteriaDto.getOriginalSymbolNumber(), incomingDocumentCriteriaDto.getSummary());
            } else {
              if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getIncomingNumber())) {
                return incomingDocumentRepository.findByIncomingNumber(incomingDocumentCriteriaDto.getIncomingNumber());
              } else {
                if(StringUtils.isNotBlank(incomingDocumentCriteriaDto.getOriginalSymbolNumber())) {
                  return incomingDocumentRepository.findByOriginalSymbolNumber(incomingDocumentCriteriaDto.getOriginalSymbolNumber());
                } else {
                  return incomingDocumentRepository.findBySummary(incomingDocumentCriteriaDto.getSummary());
                }
              }
            }
          }
        }
      }
    }

    return new ArrayList<>();
  }



}
