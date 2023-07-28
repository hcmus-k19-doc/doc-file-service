package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import java.io.IOException;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

public interface AwsS3Service {

  List<S3Object> getFiles();

  ByteArrayResource zipFilesByParentFolderAndFolderName(ParentFolderEnum parentFolder, String fileName) throws IOException;

  ByteArrayResource zipFilesByParentFolderAndFolderNameIgnoreDeleted(ParentFolderEnum parentFolder, String folderName , List<String> fileNameList) throws IOException;

  void uploadFile(ParentFolderEnum parentFolder, String folderName, MultipartFile file) throws IOException;

  GetObjectResponse uploadFile(ParentFolderEnum parentFolder, String folderName, FileWrapper file) throws IOException;

  ResponseInputStream<GetObjectResponse> getFile(ParentFolderEnum parentFolder, String folderName, String fileName);

  ResponseInputStream<GetObjectResponse> getFileFromS3Key(String fileKey);
}
