package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import java.io.IOException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public interface AwsS3Service extends ObjectStorageService {

  GetObjectResponse uploadFile(ParentFolderEnum parentFolder, String folderName, FileWrapper file) throws IOException;

  ResponseInputStream<GetObjectResponse> getFile(ParentFolderEnum parentFolder, String folderName, String fileName);

  ResponseInputStream<GetObjectResponse> getFileFromS3Key(String fileKey);

  GetObjectResponse getFile(String key);
}
