package fs.project.controller;

import fs.project.domain.User;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


@Controller
@RequiredArgsConstructor
@Slf4j
public class KakaoLoginController {

    private final fs.project.service.kakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/kakaoLogin")
    public String kakao(String code, HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info("코드 : {}", code);
        String access_Token = kakaoService.getKaKaoAccessToken(code);
        HashMap<String, Object> userInfo = kakaoService.getUserInfo(access_Token);

        HttpSession session = request.getSession();

        if (userInfo.get("email") != null) {
            session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("access_Token", access_Token);
        }

        log.info("name : {}", userInfo.get("nickname"));
        log.info("email : {}", userInfo.get("email"));
        log.info("access_token : {}", access_Token);

        Object s1 = userInfo.get("nickname");
        Object s2 = userInfo.get("email");

        String nickname =  s1.toString();
        String email = s2.toString();

        User user = userService.getNameEmail(nickname, email);

        if(user ==null){
            response.setContentType("text/html; charset=euc-kr");
            PrintWriter out = response.getWriter();

            out.println("<script>alert('가입한 정보가 없습니다.\\n카카오 아이디와 연동을 원하신다면 카카오와 연동된 E-mail로 가입하세요.'); location.href='/signUp';</script>");
            out.flush();
            return "redirect:/signUp";
        }
        else{
            session.setAttribute(SessionConst.LOGIN_USER, user);
            if(user.getMainTid()==null) return "redirect:/loginHome/0";
            else{
                String s= Long.toString(user.getMainTid());
                return "redirect:/loginHome/"+ s;
            }
        }
    }

}
