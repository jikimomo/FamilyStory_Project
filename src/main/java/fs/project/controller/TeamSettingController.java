package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.form.GroupEditForm;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TeamSettingController {

    private final UserService userService;
    private final TeamService teamService;
    private final TeamController teamController;

    // 페이지 이동 _ 내 그룹 페이지
    @GetMapping("/teamEdit")
    public String groupEditPage(@Login User loginUser, Model model, HttpServletRequest request) {
        List<Team> team =  new ArrayList<>();

        List<UserTeam> ut = teamService.findByUID(loginUser.getUID());
        for(UserTeam uteam :ut){
            if(uteam.isJoinUs()==true){
                team.add(uteam.getTeam());
            }
        }
        model.addAttribute("teams", team); // 유저가 소속된 팀
        model.addAttribute("mainChecked",loginUser.getMainTid()); // 유저의 메인팀

        //로그인 성공하였고 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하는 코드 작성.
        HttpSession session = request.getSession();
        String access_Token = (String)session.getAttribute("access_Token");
        //카카오 토큰 삭제. 왜냐? 이전 사용자가 카카오 서비스 계정 로그아웃 안하고 이후 사람이 일반 로그인 할 경우 보안에 문제가 생기기 때문에.
        if(access_Token != null && !"".equals(access_Token)) {
            model.addAttribute("kakaoLogin", true);
        }
        else model.addAttribute("kakaoLogin", false);

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        return "users/settingUserTeam";
    }

    // 기능 _ 메인팀 업데이트 (Ajax로 TID를 받아 메인팀을 업데이트한다.)
    @PostMapping("/team/editTeam")
    @ResponseBody
    public void setMainTeam(@Login User loginUser,@RequestParam("setId") String Tid, HttpServletRequest request){
        teamService.updateMainTID(loginUser.getUID(), Long.parseLong(Tid));
        User user = teamService.findUser(loginUser.getUID());
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, user);
    }

    // 페이지 이동 _ 내 그룹 페이지
    @GetMapping("/{tid}/teamEdit")
    public String groupPageEdit(@Login User loginUser, @PathVariable("tid") Long tId, Model model, HttpServletRequest request) {

        //로그인 성공하였고 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하는 코드 작성.
        HttpSession session = request.getSession();
        String access_Token = (String)session.getAttribute("access_Token");
        //카카오 토큰 삭제. 왜냐? 이전 사용자가 카카오 서비스 계정 로그아웃 안하고 이후 사람이 일반 로그인 할 경우 보안에 문제가 생기기 때문에.
        if(access_Token != null && !"".equals(access_Token)) {
            model.addAttribute("kakaoLogin", true);
        }
        else model.addAttribute("kakaoLogin", false);

        // joinUs가 False인 유저를 가져온다.
        List<User> users = userService.waitMember(tId);
        model.addAttribute("user",users);

        // joinUs가 true인 유저를 가져온다.
        List<User> teamMember =  userService.attendMember(tId);
        User GroupBoss = userService.findTeamBoss(tId);
        model.addAttribute("tId", tId);
        model.addAttribute("teamMember", teamMember); // 그룹원
        model.addAttribute("teamBoss", GroupBoss); // 그룹장

        String photoRoute = teamService.findTeam(tId).getTeamImage();
        model.addAttribute("team",teamService.findTeam(tId));

        // 저장된 있으면 저장된 이미지를, 없다면 기본 셋팅된 이미지를 넣어준다.
        if(photoRoute != null){
            model.addAttribute("photo",photoRoute);
        }else{
            photoRoute = "/AdminImage/temp.png";
            model.addAttribute("photo",photoRoute);
        }

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        Long findBossUid = userService.findBoss(tId);
        // 유저가 그룹장이라면 그룹장 관리 페이지를, 그룹장이 아니라면 그룹멤버가 보는 그룹페이지를 보여준다.
        if(loginUser.getUID()==findBossUid){
            return "team/editBossTeam";
        }else {
            return "team/editTeam";
        }
    }

    // 기능 _ 그룹 요청 수락
    @ResponseBody
    @PostMapping("/{tid}/teamEdit")
    public String AcceptMember(@Login User loginUser, @RequestParam("userId") String userId, @RequestParam("tId") Long tId, Model model){
        Long uId = teamService.findByUserID(userId).getUID();
        Long utID = teamService.findUTID(uId,tId);
        UserTeam ut = teamService.findUserTeam(utID);
        ut.setJoinUs(true);
        teamService.saveUserTeam(ut);

        User user = teamService.findByUserID(userId);
        if(user.getMainTid()==null){
            teamService.updateMainTID(user.getUID(),tId);
            teamService.updateCurTID(user.getUID(), tId);
        }

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);
        return "team/editTeam";
    }

    // 기능 _ 그룹 요청 거절
    @ResponseBody
    @PostMapping("/{tid}/deniedMember")
    public String DeniedMember(@Login User loginUser, @RequestParam("userId") String userId, @RequestParam("tId") Long tId, Model model){
        Long uId = teamService.findByUserID(userId).getUID();
        Long utID = teamService.findUTID(uId,tId);
        teamService.removeUTID(utID);

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);
        return "team/editTeam";
    }

    // 기능 _ 팀 탈퇴하기
    @GetMapping("/{tid}/teamEdit2")
    public String groupPageEdit2(@Login User loginUser, @PathVariable("tid") Long tId) {
        userService.dropTeam(loginUser.getUID(),tId);
        Long curTID;
        User user = userService.findUser(loginUser.getUID());
        if(user.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = user.getCurTid();
        }
        return "redirect:/loginHome/"+curTID;
    }

    // 기능 _ 그룹관리에서 팀 대표이미지 수정
    @PostMapping("/{tid}/updateTeamImage")
    public String updateTeamImage(@Login User loginUser, @PathVariable("tid") Long tId, @RequestParam MultipartFile file, Model model
    ) throws IOException {
        if(!file.isEmpty()){
            String fileName = teamController.renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
            String fullPath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"TeamImage"+File.separator+ fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));
            Team team = teamService.findTeam(tId);
            fullPath = File.separator + "TeamImage" + File.separator + fileName;

            // 팀 테이블에 이미지 경로 저장
            team.setTeamImage(fullPath);
            teamService.saveTeam(team);
            model.addAttribute("img",fullPath);
        }

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        return "redirect:/"+tId+"/teamEdit";
    }
}
