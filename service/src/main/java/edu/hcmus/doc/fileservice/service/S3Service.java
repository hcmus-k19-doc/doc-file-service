package edu.hcmus.doc.fileservice.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface S3Service {

  List<S3Object> getFiles();

  boolean uploadFile(MultipartFile multipartFile) throws IOException;
}
