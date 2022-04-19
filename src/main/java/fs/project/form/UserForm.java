package fs.project.form;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
@Getter
@Setter
public class UserForm {

/*    SignUpForm()에서 입력받을 멤버변수들을 생성하여 @NotEmpty 애노테이션을 주어 공백을 주지 않도록한다.
    만약 Form에서 공백으로 전송된다면 오류 message를 띄워 아래와같이 출력하도록 한다.*/

    @NotEmpty(message = "아이디는 필수 입니다")
    private String id;

    @NotEmpty(message = "비밀번호는 필수 입니다")
    private String passWord;

    @NotEmpty(message = "이름은 필수 입니다")
    private String name;

    @NotEmpty(message = "E-mail은 필수 입니다")
    private String email;

    @NotEmpty(message = "닉네임은 필수 입니다")
    private String nickName;

    @NotEmpty(message = "휴대폰 번호는 필수 입니다")
    private String phoneNumber;

    @NotEmpty(message = "생년월일은 필수 입니다")
    private String birthDay;

}
