package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.form.GroupEditForm;
import fs.project.form.LoginForm;
import fs.project.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class GroupContorller {


    private final UserService userService;
    // 최초 접근 시 해당 GetMapping을 통해서 home.html로 보여준다.
    @GetMapping("/teamEdit")
    public String groupEditPage(@Login User loginUser, Model model) {
        // 이때, "loginForm"이라는 이름을 가진 모델에 LoginForm()의 형식을 담고 간다.

        Long findUid = loginUser.getUID();
        List<Team> team =  userService.findTeam(findUid);

        for (Team t : team) {
            System.out.println("t.getTeamID() = " + t.getTeamID());
        }

        model.addAttribute("items", team);
        model.addAttribute("groupEditForm", new GroupEditForm());

//
//        List<Item> items = itemService.findItems();
//        model.addAttribute("items", items);


        return "users/settingUserTeam";
    }

    //메인그룹 변경 로직
    @PostMapping("/teamEdit1")
    public String groupPageEdit1(@Login User loginUser, @ModelAttribute GroupEditForm form) {
        userService.changeMainTeam(loginUser.getUID(), form.getChangeMainTeam());

        return "redirect:/loginHome";
    }

    //팀 탈퇴 로직
    @PostMapping("/teamEdit2")
    public String groupPageEdit2(@Login User loginUser, @ModelAttribute GroupEditForm form) {

        log.info("----------------------------------");
        userService.dropTeam(loginUser.getUID(), form.getDropTeam());
        log.info("----------------------------------");
        return "redirect:/loginHome";
    }

}
