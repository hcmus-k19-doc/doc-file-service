package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class QueueService {

  private final AwsS3Service awsS3Service;

  private final FileMapper fileMapper;

  @RabbitListener(queues = "${spring.rabbitmq.template.attachment-queue}")
  public List<FileDto> saveAttachmentsToS3(@Valid AttachmentPostDto attachmentPostDto) throws IOException {
    log.info("Received message from doc-main-service");
    log.info("Save attachment to s3 is being processed");
    log.info("Document ID: {}", attachmentPostDto.getDocId());

    List<FileDto> fileDtos = new ArrayList<>();
    for (FileWrapper fileWrapper : attachmentPostDto.getAttachments()) {
      GetObjectResponse fileFromS3 = awsS3Service.uploadFile(
            attachmentPostDto.getParentFolder(),
            attachmentPostDto.getDocId().toString(),
            fileWrapper);

      fileDtos.add(fileMapper.toDto(
          attachmentPostDto.getParentFolder().value,
          attachmentPostDto.getDocId().toString(),
          fileFromS3)
      );
    }

    return fileDtos;
  }
}
