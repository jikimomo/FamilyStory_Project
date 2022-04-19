package fs.project.domain;

import lombok.Data;

@Data
public class File {

    private Long Id;
    private Content.UploadFile attachFile;

}
