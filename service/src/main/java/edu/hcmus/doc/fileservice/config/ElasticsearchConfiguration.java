package edu.hcmus.doc.fileservice.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

  private final ElasticsearchProperties elasticsearchProperties;

  @Override
  @Bean
  public RestHighLevelClient elasticsearchClient() {

    final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .connectedTo(elasticsearchProperties.getUris().toArray(new String[0]))
        .withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
        .build();

    return RestClients.create(clientConfiguration).rest();
  }
}