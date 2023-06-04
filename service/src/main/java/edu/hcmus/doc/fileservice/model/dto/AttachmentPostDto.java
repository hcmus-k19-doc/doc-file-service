package edu.hcmus.doc.fileservice.model.dto;

import edu.hcmus.doc.fileservice.model.enums.ParentFolderEnum;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttachmentPostDto {

  @NotNull
  private Long docId;
  @NotNull
  private ParentFolderEnum parentFolder;
  @NotNull
  private List<@Valid FileWrapper> attachments;
}
