package fs.project.form;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ContentInputVO {

    // content 내용 자체에 관한 vo객체 -> upload, update할 때 사용
    private String photoRoute;
    private String explanation;
    private String location;
    private LocalDate when;

    public ContentInputVO(String photoRoute, String explanation, String location, LocalDate when){
        this.photoRoute = photoRoute;
        this.explanation = explanation;
        this.location = location;
        this.when = when;
    }
}
