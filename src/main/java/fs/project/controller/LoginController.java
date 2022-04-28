package fs.project.controller;

import fs.project.domain.User;
import fs.project.form.LoginForm;
import fs.project.service.KakaoService;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;
    private final KakaoService kakaoService;


//    @PostMapping("/")
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
//        return "redirect:/loginHome";

        String access_Token = (String)session.getAttribute("access_Token");
        //카카오 토큰 삭제. 왜냐? 이전 사용자가 카카오 서비스 계정 로그아웃 안하고 이후 사람이 일반 로그인 할 경우 보안에 문제가 생기기 때문에.
        if(access_Token != null && !"".equals(access_Token)) {
            kakaoService.kakaoLogout(access_Token);
            session.removeAttribute("access_Token");
            session.removeAttribute("userId");
        }

        Long mainTID = loginUser.getMainTid();
        Long curTID = loginUser.getCurTid();
        Long tID;

        if(mainTID == null){
            tID = 0L;
        }
        else{
            if(curTID == null){
                tID = mainTID;
            }
            else{
                tID = curTID;
            }
        }
        return "redirect:/loginHome/"+tID;
    }


    //로그아웃 버튼 클릭 시
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        //세션 값을 담아온다.
        log.info("로그아웃");
        HttpSession session = request.getSession(false);
        String access_Token = (String)session.getAttribute("access_Token");

        //카카오 토큰 삭제
        if(access_Token != null && !"".equals(access_Token)) {
            kakaoService.kakaoLogout(access_Token);
            session.removeAttribute("access_Token");
            session.removeAttribute("userId");
        }

        //현재 담겨져있는 세션값이 존재한다면 세션을 드랍한다.
        if(session !=null){
            session.invalidate();
        }
        // 로그인 페이지로 이동
        return "redirect:/";
    }


    @PostMapping("/")
    @ResponseBody
    public String loginCheck(HttpServletRequest request) {

        String id = request.getParameter("id");
        String pw = request.getParameter("pw");

        log.info("id : {}", id);
        log.info("pw : {}", pw);

        User loginUser = userService.login(id, pw);

        if (loginUser == null) {

            return "아이디 또는 비밀번호가 일치하지 않습니다.";
        }
        else{

            HttpSession session = request.getSession();
            // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
            // 즉, 로그인 회원 정보를 세션에 담아놓는다.
            session.setAttribute(SessionConst.LOGIN_USER, loginUser);
//"loginUser"
            // users/login으로 매핑하는 컨트롤러를 찾아간다. (HomeController에 있다)
//        return "redirect:/loginHome";

            String access_Token = (String)session.getAttribute("access_Token");
            //카카오 토큰 삭제. 왜냐? 이전 사용자가 카카오 서비스 계정 로그아웃 안하고 이후 사람이 일반 로그인 할 경우 보안에 문제가 생기기 때문에.
            if(access_Token != null && !"".equals(access_Token)) {
                kakaoService.kakaoLogout(access_Token);
                session.removeAttribute("access_Token");
                session.removeAttribute("userId");
            }

            Long mainTID = loginUser.getMainTid();
            Long curTID = loginUser.getCurTid();
            Long tID;

            if(mainTID == null){
                tID = 0L;
            }
            else{
                if(curTID == null){
                    tID = mainTID;
                }
                else{
                    tID = curTID;
                }
            }
            String Msg = "/loginHome/"+tID;
            return Msg;

        }
    }

//        int insertRst = sqlSession.insert("login.insert", loginVO);



}
