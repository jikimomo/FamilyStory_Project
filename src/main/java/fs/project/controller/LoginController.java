package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.User;
import fs.project.form.LoginForm;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;

    @PostMapping("/")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult result, HttpServletRequest request) {
        // @Valid 는 BindingResult result 검증작업에 사용하는 애노테이션이다.
        // @ModelAttribute는 html에서 받아온 데이터 값들을 UserForm form에 고대로 넣어준다.
        if (result.hasErrors()) {
            //result에 에러가 있다면 다시 home.html로 보낸다. 이때 form에서 message 문장들을 출력해준다.
            return "home";
        }

        //입력받은 아이디와 패스워드를 userService의 login 메소드에 전달하고 데이터베이스의 정보와 일치한지 확인한다.
        //이후 일치한다면 그 객체를 반환해서 loginUser에 담아주고 일치하지않는다면 null값을 넣어준다.
        User loginUser = userService.login(form.getLoginId(), form.getPassword());

        if (loginUser == null) {
            //오류 출력
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다. ");
            //다시 home.html 반환
            return "home";
        }
        //로그인 성공하였고 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하는 코드 작성.
        HttpSession session = request.getSession();

        // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
        // 즉, 로그인 회원 정보를 세션에 담아놓는다.
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);
        //"loginUser"
        // users/login으로 매핑하는 컨트롤러를 찾아간다. (HomeController에 있다)
        // return "redirect:/loginHome";
        return "redirect:/loginHome";
    }

    //로그아웃 버튼 클릭 시
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        //세션 값을 담아온다.
        HttpSession session = request.getSession(false);
        //현재 담겨져있는 세션값이 존재한다면 세션을 드랍한다.
        if(session !=null){
            session.invalidate();
        }
        // 로그인 페이지로 이동
        return "redirect:/";
    }



}
