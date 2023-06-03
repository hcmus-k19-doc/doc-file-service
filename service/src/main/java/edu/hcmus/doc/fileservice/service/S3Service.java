package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface S3Service {

  List<S3Object> getFiles();

  ByteArrayResource downloadFilesByParentFolderAndFolderName(ParentFolderEnum parentFolder, String fileName) throws IOException;

  void uploadFile(ParentFolderEnum parentFolder, String folderName, MultipartFile file) throws IOException;
}
