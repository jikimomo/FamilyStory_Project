package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.form.GroupEditForm;
import fs.project.form.LoginForm;
import fs.project.service.TeamService;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class GroupContorller {

    private final UserService userService;
    private final TeamService teamService;

    // 최초 접근 시 해당 GetMapping을 통해서 home.html로 보여준다.
    @GetMapping("/teamEdit")
    public String groupEditPage(@Login User loginUser, Model model) {
        // 이때, "loginForm"이라는 이름을 가진 모델에 LoginForm()의 형식을 담고 간다.

        // 세션에 대한 정보 중에 유아디값을 얘한테 받아서 저장한다
        Long findUid = loginUser.getUID();
        // findUid를 들고 유저서비스에 구현된 findTeam이라는 메서드로 찾아간다
        List<Team> team =  userService.findTeam(findUid);

        for (Team t : team) {
            System.out.println("t.getTeamID() = " + t.getTeamID());
        }

        model.addAttribute("teams", team);
        model.addAttribute("mainChecked",loginUser.getMainTid());
        model.addAttribute("groupEditForm", new GroupEditForm());

//
//        List<Item> items = itemService.findItems();
//        model.addAttribute("items", items);


        return "users/settingUserTeam";
    }


    @PostMapping("/team/editTeam")
    @ResponseBody
    public void setMainTeam(@Login User loginUser,@RequestParam("setId") String mainTeam,HttpServletRequest request){
        Long Tid = teamService.findByTeamID(mainTeam);
        teamService.updateMainTeamID(loginUser.getUID(),Tid);
        User user = teamService.findUser(loginUser.getUID());
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, user);

    }


    //그룹 관리
    @GetMapping("/{tid}/teamEdit")
    public String groupPageEdit(@Login User loginUser, @PathVariable("tid") Long tId, Model model) {

        log.info("--------------------{}--------------------------", tId);

        Long findBossUid = userService.findBoss(tId);

        //만약 현재 사용자가 그룹의 boss라면 그룹의 관리자 페이지 (그룹 탈퇴하기, 메인 그룹 설정화면, 이벤트 추가 등등)

        // joinUs가 False인 유저를 가져온다.
        List<User> users = userService.waitMember(tId);
        model.addAttribute("user",users);

        // joinUs가 true인 유저를 가져온다.
        List<User> teamMember =  userService.attendMember(tId);
        User GroupBoss = userService.findTeamBoss(tId);
        model.addAttribute("tId", tId);
        model.addAttribute("teamMember", teamMember);
        model.addAttribute("teamBoss", GroupBoss);

        if(loginUser.getUID()==findBossUid){
            return "team/editBossTeam";
        }

        //만약 현재 사용자가 그룹의 boss가 아니라면 그룹 탈퇴하기 및 메인 그룹 설정 화면
        else {
            return "team/editTeam";
        }

    }

    @PostMapping("/{tid}/teamEdit")
    public String AcceptMember(@RequestParam("userId") String userId, @RequestParam("tId") Long tId){
        UserTeam ut = teamService.findUserTeam(userId,tId).get(0);
        ut.setJoinUs(true);
        teamService.saveUserTeam(ut);

        User user = teamService.findByUserID(userId).get(0);
        if(user.getMainTid()==null){
            teamService.updateMainTeamID(user.getUID(),tId);
        }
        return "team/editTeam";
    }
    @PostMapping("/{tid}/deniedMember")
    public String DeniedMember(@RequestParam("userId") String userId, @RequestParam("tId") Long tId){
        UserTeam ut = teamService.findUserTeam(userId,tId).get(0);
        teamService.removeUTID(ut.getUtID());

        return "team/editTeam";
    }




//    메인그룹 변경 로직
    @GetMapping("/{tid}/teamEdit1")
    public String groupPageEdit1(@Login User loginUser,  @PathVariable("tid") Long tId, HttpServletRequest request) {

        userService.changeMainTeam(loginUser.getUID(),tId);
        HttpSession session = request.getSession();

        // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
        // 즉, 로그인 회원 정보를 세션에 담아놓는다.
        User user = userService.findOne(loginUser.getUID());
        session.setAttribute(SessionConst.LOGIN_USER, user);
        return "redirect:/loginHome";
    }

    //팀 탈퇴 로직
    @GetMapping("/{tid}/teamEdit2")
    public String groupPageEdit2(@Login User loginUser,  @PathVariable("tid") Long tId, HttpServletRequest request) {

        userService.dropTeam(loginUser.getUID(),tId);

        return "redirect:/loginHome";
    }

}
