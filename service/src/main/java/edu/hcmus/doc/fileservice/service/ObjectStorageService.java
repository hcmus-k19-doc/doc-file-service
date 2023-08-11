package edu.hcmus.doc.fileservice.service;

import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {

  ByteArrayResource zipFilesByParentFolderAndFolderName(ParentFolderEnum parentFolder, String folderName) throws IOException;

  ByteArrayResource zipFilesByParentFolderAndFolderNameIgnoreDeleted(ParentFolderEnum parentFolder, String folderName , List<String> fileNameList) throws IOException;

  void uploadFile(ParentFolderEnum parentFolder, String folderName, MultipartFile file)
      throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
