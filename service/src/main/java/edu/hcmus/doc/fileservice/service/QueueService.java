package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.exception.DocFileServiceRuntimeException;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate.RabbitConverterFuture;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class QueueService {

  @Value("${spring.rabbitmq.template.exchange}")
  private String exchange;

  @Value("${spring.rabbitmq.template.attachment-routing-key}")
  private String routingKey;

  @Value("${spring.rabbitmq.template.s3-attachment-routing-key}")
  private String s3AttachmentRoutingKey;

  private final AwsS3Service awsS3Service;

  private final AsyncRabbitTemplate asyncRabbitTemplate;

  @RabbitListener(queues = "${spring.rabbitmq.template.attachment-queue}")
  public void saveAttachmentsToS3(@Valid AttachmentPostDto attachmentPostDto) throws IOException {
    log.info("Received message from doc-main-service");
    log.info("Save attachment to s3 is being processed");
    log.info("Document ID: {}", attachmentPostDto.getDocId());

    for (FileWrapper fileWrapper : attachmentPostDto.getAttachments()) {
        awsS3Service.uploadFile(
            attachmentPostDto.getParentFolder(),
            attachmentPostDto.getDocId().toString(),
            fileWrapper);
    }
  }

  public void sendAttachmentsToQueue(AttachmentPostDto attachmentPostDto) {
    RabbitConverterFuture<List<FileDto>> rabbitConverterFuture = asyncRabbitTemplate
        .convertSendAndReceiveAsType(exchange, s3AttachmentRoutingKey, attachmentPostDto,
            new ParameterizedTypeReference<>() {
            }
        );

    rabbitConverterFuture.addCallback(
        new ListenableFutureCallback<>() {
          @Override
          public void onFailure(Throwable ex) {
            throw new DocFileServiceRuntimeException("Error while sending message through RabbitMQ", ex);
          }

          @Override
          public void onSuccess(List<FileDto> result) {
            log.info("Received message from doc-main-service: {}", result);
          }
        }
    );
  }
}
