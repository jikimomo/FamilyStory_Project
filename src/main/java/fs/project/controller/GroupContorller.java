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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class GroupContorller {


    private final UserService userService;
    private final TeamService teamService;
    private final TeamController teamController;

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

        String photoRoute = teamService.findTeam(tId).getTeamImage();

        model.addAttribute("team",teamService.findTeam(tId));


        if(photoRoute != null){
            model.addAttribute("photo",photoRoute);
        }
        else{
            photoRoute = "/AdminImage/temp.png";
            model.addAttribute("photo",photoRoute);
        }

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


////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 기능 _ 팀 그룹 생성 시 이미지 업로드 & DB 저장
    @PostMapping("/{tid}/updateTeamImage")
    public String updateTeamImage(@PathVariable("tid") Long tId, @RequestParam MultipartFile file, Model model
    ) throws IOException {
        System.out.println("upload Controller");
        // 로그로 파일 넘어온 내용을 체크했다.
//        log.info("multi={}",file);
        String str ="";
        if(!file.isEmpty()){
            String fileName = teamController.renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
//            String fullPath = fileDir + fileName;
            String fullPath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"TeamImage"+File.separator+ fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));

            Team team = teamService.findTeam(tId);

            fullPath = File.separator + "TeamImage" + File.separator + fileName;

            // 팀 테이블에 이미지 경로 저장
            team.setTeamImage(fullPath);
            teamService.saveTeam(team);
            str=fullPath;
            model.addAttribute("img",fullPath);
        }

        return "redirect:/"+tId+"/teamEdit";
    }

}

