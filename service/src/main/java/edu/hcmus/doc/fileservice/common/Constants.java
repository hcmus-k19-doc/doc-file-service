package edu.hcmus.doc.fileservice.common;

import edu.hcmus.doc.fileservice.model.enums.FileType;
import java.util.List;

public class Constants {

  public static final List<String> ALLOWED_FILE_TYPES = List.of(
      FileType.PDF.value,
      FileType.JPG.value,
      FileType.PNG.value
  );
}
