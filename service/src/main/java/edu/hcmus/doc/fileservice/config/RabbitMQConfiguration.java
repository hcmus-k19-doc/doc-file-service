package edu.hcmus.doc.fileservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitMQConfiguration {

  @Value("${spring.rabbitmq.template.default-receive-queue}")
  private String queue;

  @Value("${spring.rabbitmq.template.attachment-queue}")
  private String attachmentQueue;

  @Value("${spring.rabbitmq.template.exchange}")
  private String exchange;

  @Value("${spring.rabbitmq.template.routing-key}")
  private String routingKey;

  @Value("${spring.rabbitmq.template.attachment-routing-key}")
  private String attachmentRoutingKey;

  @Value("${spring.rabbitmq.username}")
  private String username;

  @Value("${spring.rabbitmq.password}")
  private String password;

  @Value("${spring.rabbitmq.host}")
  private String host;

  @Bean
  Queue docQueue() {
    return new Queue(queue, true);
  }

  @Bean
  Queue attachmentQueue() {
    return new Queue(attachmentQueue, true);
  }

  @Bean
  Exchange docExchange() {
    return ExchangeBuilder.directExchange(exchange).durable(true).build();
  }

  @Bean
  Binding binding() {
    return BindingBuilder
        .bind(docQueue())
        .to(docExchange())
        .with(routingKey)
        .noargs();
  }

  @Bean
  Binding attachmentBinding() {
    return BindingBuilder
        .bind(attachmentQueue())
        .to(docExchange())
        .with(attachmentRoutingKey)
        .noargs();
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
    cachingConnectionFactory.setUsername(username);
    cachingConnectionFactory.setPassword(password);
    return cachingConnectionFactory;
  }

  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public AsyncRabbitTemplate asyncRabbitTemplate(){
    return new AsyncRabbitTemplate(rabbitTemplate(connectionFactory()));
  }
}
