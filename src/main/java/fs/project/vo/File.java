package fs.project.vo;

import fs.project.domain.Content;
import lombok.Data;

@Data
public class File {

    private Long Id;
    private Content.UploadFile attachFile;

}
