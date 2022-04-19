package fs.project.controller;

import fs.project.domain.User;
import fs.project.form.LoginForm;
import fs.project.form.UserForm;
import fs.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SignUpController {

    private final UserService userService;

    //회원가입 페이지로 이동
    @GetMapping("/signUp")
    public String createForm(Model model) {
        // userForm 이라는 이름을 가진 모델에 UserForm 객체를 담는다.
        model.addAttribute("userForm", new UserForm());
        // createUserForm.html로 보냄.
        return "users/createUserForm";
    }

    @PostMapping("/signUp")
    public String create(@Valid @ModelAttribute UserForm form, BindingResult result, Model model) {
        // @Valid 는 BindingResult result 검증작업에 사용하는 애노테이션이다.
        // @ModelAttribute는 html에서 받아온 데이터 값들을 UserForm form에 고대로 넣어준다.
        if (result.hasErrors()) {
            //result에 에러가 있다면 다시 createUserForm.html로 보낸다. 이때 form에서 message 문장들을 출력해준다.
            return "users/createUserForm";
        }

        // 에러가 없다면 아래와 같이 도메인의 User에 폼에서 받아온 데이터들을 주입한다.
        User user = new User();
        user.setUserID(form.getId());
        user.setPassword(form.getPassWord());
        user.setName(form.getName());
        user.setNickName(form.getNickName());
        user.setEmail(form.getEmail());
        user.setPhoneNumber(form.getPhoneNumber());

        // 생일 같은 경우에는 LocalDate타입이기 때문에 포맷터를 이용해서 스트링을 LocalDate 타입으로 전환해주고 주입한다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(form.getBirthDay(), formatter);
        user.setBirthday(localDate);

        // userService에 구현된 join메소드를 활용해서 데이터베이스에 해당 객체를 주입을 시도한다.
        // 만약 주입하려던 객체의 ID값이 이미 존재한다면 null을 반환한다.
        // 그렇지 않다면 주입해서 데이터베이스에 저장한다.
        Optional<User> signUser = userService.join(user);

        // 반환값이 null로 됐다면 "이미 존재하는 아이디입니다" 라는 문구를 명시해준다.
        if (signUser == null) {
            result.reject("signupFail", "이미 존재하는 아이디입니다 ");
            return "users/createUserForm";
        }

        // 데이터베이스에 정상적으로 주입되었다면 다시 홈화면으로 들어간다.
        model.addAttribute("loginForm", new LoginForm());
        return "redirect:/";
    }
}
