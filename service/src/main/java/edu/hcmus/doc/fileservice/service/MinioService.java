package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.dto.FileWrapper;
import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import io.minio.GetObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface MinioService extends ObjectStorageService {

  GetObjectResponse uploadFile(ParentFolderEnum parentFolder, String folderName, FileWrapper file)
      throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

  GetObjectResponse getFile(String key)
      throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
