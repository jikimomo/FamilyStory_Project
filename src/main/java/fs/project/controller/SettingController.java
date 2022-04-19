package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.User;
import fs.project.form.UserSetForm;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
@RequiredArgsConstructor
@Slf4j
public class SettingController {

    private final UserService userService;

    @GetMapping("/users/settinguser")
    public String updateUser(@Login User loginUser, Model model) {
        // 이때, "loginForm"이라는 이름을 가진 모델에 LoginForm()의 형식을 담고 간다.
        UserSetForm userSetForm = new UserSetForm();
        userSetForm.setId(loginUser.getUserID());
        userSetForm.setPassword(loginUser.getPassword());
        userSetForm.setPasswordCheck(loginUser.getPassword());
        userSetForm.setName(loginUser.getName());
        userSetForm.setNickName(loginUser.getNickName());
        userSetForm.setEmail(loginUser.getEmail());
        userSetForm.setPhoneNumber(loginUser.getPhoneNumber());
        userSetForm.setImage(loginUser.getUserImage());
        model.addAttribute("userSetForm", userSetForm);
        return "users/settingUser";
    }

    @PostMapping("/users/settinguser")
    public String updateUser(@Login User loginUser, @ModelAttribute UserSetForm form, Model model, HttpServletRequest request) {

        Long updateUid = loginUser.getUID();

        userService.updateUser(updateUid, form);

        HttpSession session = request.getSession();

        session.setAttribute(SessionConst.LOGIN_USER, userService.findOne(updateUid));

        return "users/settingUserComplete";

    }
}