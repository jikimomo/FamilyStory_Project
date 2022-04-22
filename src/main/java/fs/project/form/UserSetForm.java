package fs.project.form;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@Data
public class UserSetForm {

    @NotEmpty(message = "아이디는 필수입니다.")
    private String id;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String password;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String passwordCheck;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String name;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String nickName;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String email;
    @NotEmpty(message = "아이디는 필수입니다.")
    private String phoneNumber;


    private List<MultipartFile> userImage;

    private List<MultipartFile> userCoverImage;

}
