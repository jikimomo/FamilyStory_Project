package fs.project.domain;

import lombok.Data;

@Data
public class UploadFile {

    private String uploadFileName; // 업로드된 파일명
    private String storeFileName; // DB에 저장할 파일명

    public UploadFile(String uploadFileName, String storeFileName){
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
