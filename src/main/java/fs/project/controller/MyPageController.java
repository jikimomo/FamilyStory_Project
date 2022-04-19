package fs.project.controller;

import fs.project.domain.User;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MyPageController {

    // 마이페이지 버튼 클릭 시
    @GetMapping("/myPage")
    public String myPage3(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model){
        // LOGIN_USER 상자에 담긴 객체를 꺼내서 loginUser에 주입.
        if (loginUser == null){
            //null이면 현재 저장된 세션 없으니 로그인페이지로 이동.
            return "redirect:/";
        }
        //null이 아니라면 해당 객체를 "myInformation" 이름을 가진 모델에 넣어서 myPage.html로 간다.
        model.addAttribute("myInformation", loginUser);
        return "users/myPage";
    }
}
