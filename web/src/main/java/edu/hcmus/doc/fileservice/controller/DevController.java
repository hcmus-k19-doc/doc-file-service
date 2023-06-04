package edu.hcmus.doc.fileservice.controller;


import edu.hcmus.doc.fileservice.DocURL;
import edu.hcmus.doc.fileservice.model.dto.AttachmentPostDto;
import edu.hcmus.doc.fileservice.service.QueueService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(DocURL.API_V1 + "/dev")
public class DevController {

  private final QueueService queueService;

  @PostMapping("/rabbitmq/attachment")
  public void saveAttachments(@Valid AttachmentPostDto attachmentPostDto) {
    queueService.sendAttachmentsToQueue(attachmentPostDto);
  }
}
