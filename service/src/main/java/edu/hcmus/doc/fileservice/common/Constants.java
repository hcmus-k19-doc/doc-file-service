package edu.hcmus.doc.fileservice.common;

import edu.hcmus.doc.fileservice.model.enums.FileTypeEnum;
import java.util.List;

public class Constants {

  public static final List<String> ALLOWED_FILE_TYPES = List.of(
      FileTypeEnum.PDF.value,
      FileTypeEnum.JPG.value,
      FileTypeEnum.JPEG.value,
      FileTypeEnum.PNG.value
  );
}
