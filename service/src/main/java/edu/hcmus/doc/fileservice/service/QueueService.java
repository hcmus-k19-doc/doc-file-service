package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.AttachmentPostDto;
import edu.hcmus.doc.fileservice.model.dto.FileDto;
import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.util.mapper.FileMapper;
import io.minio.GetObjectResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Service
public class QueueService {

  private final MinioService minioService;

  private final FileMapper fileMapper;

  @SneakyThrows
  @RabbitListener(queues = "${spring.rabbitmq.template.attachment-queue}")
  public List<FileDto> saveAttachmentsToS3(@Valid AttachmentPostDto attachmentPostDto) {
    log.info("Received message from doc-main-service");
    log.info("{}: Save attachment is processing...", minioService.getClass());
    log.info("Document type: {}. Document ID: {}", attachmentPostDto.getParentFolder(), attachmentPostDto.getDocId());

    List<FileDto> fileDtos = new ArrayList<>();
    for (FileWrapper fileWrapper : attachmentPostDto.getAttachments()) {
      GetObjectResponse fileFromMinio = minioService.uploadFile(
            attachmentPostDto.getParentFolder(),
            attachmentPostDto.getDocId().toString(),
            fileWrapper);

      fileDtos.add(fileMapper.toDto(
          attachmentPostDto.getParentFolder().value,
          attachmentPostDto.getDocId(),
          fileWrapper.getFileName(),
          fileFromMinio)
      );
    }

    return fileDtos;
  }
}
