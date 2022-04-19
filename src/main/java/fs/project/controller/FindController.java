package fs.project.controller;

import fs.project.domain.User;
import fs.project.form.FindIdForm;
import fs.project.form.FindPwForm;
import fs.project.form.UserForm;
import fs.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FindController {

    private final UserService userService;
    @GetMapping("/users/findId")
    public String findId1(Model model) {
        model.addAttribute("findIdForm", new UserForm());
        return "users/findIdForm";
    }

    @PostMapping("/users/findId")
    public String findId2(@Valid FindIdForm form, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "users/findIdForm";
        }
        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());

        Optional<User> findUser = userService.findId(user);

        if (findUser == null) {
            result.reject("signupFail", "존재하지 않는 아이디 입니다. ");
            return "users/findIdForm";
        } else {
            String s = findUser.get().getUserID();
            model.addAttribute("findId", s);
            return "users/findId";
        }
    }


    @GetMapping("/users/findPw")
    public String findPw1(Model model) {
        model.addAttribute("findPwForm", new UserForm());
        return "users/findPwForm";
    }

    @PostMapping("/users/findPw")
    public String findPw2(@Valid FindPwForm form, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "users/findPwForm";
        }
        User user = new User();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setUserID(form.getId());

        Optional<User> findUser = userService.findPw(user);


        if (findUser == null) {
            result.reject("signupFail", "입력 내용을 확인하세요. ");
            return "users/findPwForm";
        } else {

            String s = findUser.get().getPassword();
            String str = userService.getTempPassword();

            FindPwForm fpf = new FindPwForm();

            fpf.setEmail(findUser.get().getEmail());
            fpf.setName(findUser.get().getName());
            fpf.setId(findUser.get().getUserID());


            fpf.setAddress(findUser.get().getEmail());
            fpf.setTitle(findUser.get().getName()+"님의 Family Story 임시비밀번호 안내 이메일 입니다.");
            fpf.setMessage("안녕하세요!\n\n Family Story 임시비밀번호 안내 관련 이메일 입니다.\n"+ "\n" + "[" + findUser.get().getUserID() + "]" +"님의 임시 비밀번호는 " + str + " 입니다.");

            userService.mailSend(fpf);

//            log.info("{}", findUser.get().getEmail());
//            log.info("{}", fpf.getEmail());
//            log.info("{}", fpf.getName());

            userService.editPassword(findUser.get().getUID(), str);

            model.addAttribute("findPw", s);
            model.addAttribute("newPw", str);

            return "users/findPw";
        }
    }
}
