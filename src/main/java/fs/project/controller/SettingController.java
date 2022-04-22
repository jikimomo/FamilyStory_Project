package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.form.GroupEditForm;
import fs.project.form.UserSetForm;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SettingController {

    private final UserService userService;

    //내 정보 수정 불러오기
    //"users/settinguser"라는 url로 get매핑이 잡히고
    //settingUser.html에 보낼 updateUser메소드안에 userSetForm이라는 박스(객체)를 만들고(UserSetForm이라는 클래스로 getter,setter정의되어 있음 이 매개변수들을 가져와서 만든 객체에 가져오는것)
    //set~해서 현재 로그인된 유저의 정보를 string타입으로 정보를 get해서 받아오고 그 정보를
    //model.addAttribute의"userSetForm"이라는 이름으로 설정된 객체에 담아서 리턴하고
    //리턴되는 곳은 users/settingUser라고 되어있는 html이다.
    @GetMapping("/users/settinguser")
    public String updateUser(@Login User loginUser, Model model) {
        //@Login User loginUser에 현재 로그인된 로그인 세션이 담겨져 있다.
        UserSetForm userSetForm = new UserSetForm();
        userSetForm.setId(loginUser.getUserID());
        userSetForm.setPassword(loginUser.getPassword());
        userSetForm.setPasswordCheck(loginUser.getPassword());
        userSetForm.setName(loginUser.getName());
        userSetForm.setNickName(loginUser.getNickName());
        userSetForm.setEmail(loginUser.getEmail());
        userSetForm.setPhoneNumber(loginUser.getPhoneNumber());
        userSetForm.setImage(loginUser.getUserImage());
        //
        model.addAttribute("userSetForm", userSetForm);
        return "users/settingUser";
    }

    //내 정보 수정하기 업데이트 로직
    //settingUser.html에서 수정하기를 누르면 UserSetForm이라는 객체의 form에 내용이 담겨져 오고
    //loginUser.getUID()로 현재 로그인된 uID찾고 userService의 updateUser로 정보를 보낸다 -> userService로 이동
    @PostMapping("/users/settinguser")
    public String updateUser(@Login User loginUser, @ModelAttribute UserSetForm form, Model model, HttpServletRequest request) {

        //Long타입의 updateUid라는 객체에 현재 로그인된 유저의 ID를 getUID로 가져온다.
        Long updateUid = loginUser.getUID();

        //userService의 updateUser라는 메소드를 찾아가고 form에 담는다?
        userService.updateUser(updateUid, form);

        //HttpSession에 관한 코드
        HttpSession session = request.getSession();

        //session에다가 LOGIN_USER라는 박스에 findOne(updateUid)를 담고 setAttribute해준다.
        session.setAttribute(SessionConst.LOGIN_USER, userService.findOne(updateUid));

        //"users/settingUserComplete" view페이지로 return
        return "users/settingUserComplete";

    }


    //회원탈퇴
    @GetMapping("/deleteUser")
    public String deleteUser(@Login User loginUser, HttpServletRequest request) {

        userService.deleteUser(loginUser.getUID());

        return "redirect:/";

    }

}