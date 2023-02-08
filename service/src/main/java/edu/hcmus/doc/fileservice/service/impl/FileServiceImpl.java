package edu.hcmus.doc.fileservice.service.impl;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import edu.hcmus.doc.fileservice.service.FileService;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

  @Override
  public List<String> getFileTitles() {
    return Collections.emptyList();
  }
}
