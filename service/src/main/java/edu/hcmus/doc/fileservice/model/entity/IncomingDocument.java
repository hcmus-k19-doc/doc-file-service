package edu.hcmus.doc.fileservice.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "logstash-incoming-document-index")
@Data
public class IncomingDocument {
  @Id
  private final Long id;

  @Field(type = FieldType.Text, name = "incoming_number")
  private final String incomingNumber;

  @Field(type = FieldType.Text, name = "original_symbol_number")
  private final String originalSymbolNumber;

  @Field(type = FieldType.Text, name = "summary")
  private final String summary;


}
