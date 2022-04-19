package fs.project.form;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@Data
public class LoginForm {

    //LoginForm()에는 loginId와 password를 String형태로 담고 있으며, @NotEmpty 애노테이션을 줌으로써 공백 입력시 오류를 표시한다.
    @NotEmpty(message = "아이디는 필수입니다.")
    private String loginId;
    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password;

}
