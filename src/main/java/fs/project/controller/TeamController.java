package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.form.TeamForm;
import fs.project.service.TeamService;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import fs.project.vo.BaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TeamController extends BaseEntity {

    private final TeamService teamService;
    private final UserService userService;

    // 페이지 이동 _ 회원가입 후 이동할 페이지
    @GetMapping("/AfterJoin")
    public String AfterJoin(@Login User loginUser, Model model, HttpServletRequest request) {
        System.out.println("AfterJoin Page");

        HttpSession session = request.getSession();
        String access_Token = (String)session.getAttribute("access_Token");
        //카카오 로그인인지 아닌지 확인 로직
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

        return "AfterJoin";
    }

    // 페이지 이동 _ 그룹 생성 클릭 후 페이지로 이동
    @GetMapping("/CreateTeam")
    public String CreateTeam(Model model, @Login User loginUser) {
        System.out.println("CreateTeam Page");

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);
        model.addAttribute("TeamForm", new Team());
        model.addAttribute("user", loginUser.getUserID());
        return "/CreateTeam";
    }

    // 기능 _ 뷰에서 구성원 추가시 구성원 여부
    @ResponseBody
    @PostMapping(value = "/validateMem")
    public int validateMem(@RequestParam("sendData") String id){
        if(id==""){
            return -1;
        }else{
            return teamService.UserIdCheck(id);
        }
    }

    // 기능 _ 뷰에서 그룹ID 입력시 그룹ID 중복 여부
    @ResponseBody
    @PostMapping(value = "/validateTeam")
    public int validateTeam(@RequestParam("sendData") String id){
        if(id==""){
            return -1;
        }
        return teamService.TeamIdCheck(id);
    }

    // 기능 _ 속해있는 그룹이 없으면 메인그룹체크를 강제로 하기 위함.
    @ResponseBody
    @PostMapping(value="/mainTeamChecked")
    public int mainTeamChecked(@Login User loginuser){
        List<UserTeam> userTeamList = teamService.findByUID(loginuser.getUID());
        int res = 0;
        for(int i=0; i<userTeamList.size();i++){
            if(userTeamList.get(i).isJoinUs()){
                res++;
            }
        }

        return res;
    }

    // 기능 _ 폼 데이터 DB에 저장
    @ResponseBody
    @PostMapping(value = "/CreateTeam")
    public void  CreateTeamForm(@Valid TeamForm teamForm,@Login User loginUser, HttpServletRequest request) {
        System.out.println("CreateTeam Controller");

        // 전달받은 데이터를 Team 테이블에 저장
        Team team = new Team();
        team.setTeamID(teamForm.getTeamId());
//        team.setTeamName(teamForm.getTeamName());
        team.setBoss(loginUser.getUID());
        Long saveId = teamService.saveTeam(team);
        Team findTeam = teamService.findTeam(saveId);
        if(teamForm.isMainTeamChecked()){
            teamService.updateMainTID(loginUser.getUID(),findTeam.getTID());

            User user = teamService.findUser(loginUser.getUID());

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_USER, user);
        }

        if (saveId != 0) {
//            Team findT = teamService.findTeam(saveId);
            // 기념일 정보 저장 ( 데이터가 여러개 일 수 있어서 반복문 사용 )
            for (int i = 0; i < teamForm.getEventName().length; i++) {
                if(teamForm.getEventName()[i] != ""){
                    // 전달받은 데이터를 TeamEvent 테이블에 저장
                    TeamEvent te = new TeamEvent();
                    te.setTeam(findTeam);
                    te.setEventName(teamForm.getEventName()[i]);
                    // DB에서 날짜 타입이 LocalDate이기 때문에 문자열로 받은 내용을 파싱.
                    LocalDate date = LocalDate.parse(teamForm.getEventDate()[i], DateTimeFormatter.ISO_DATE);
                    te.setEventDate(date);
                    teamService.saveTeamEvent(te);
                }
            }

            UserTeam ut = new UserTeam();
            ut.setTeam(findTeam);
            ut.setUser(loginUser);
            ut.setJoinUs(true); // 팀을 만든 사람은 가입 허락이 필요없다.
            ut.setJoinTime(LocalDateTime.now());
            teamService.saveUserTeam(ut);

            // 구성원 저장 ( 데이터가 여러개 일 수 있어서 반복문 사용 )
            for (int i = 0; i < teamForm.getUsers().length; i++) {
                if( teamForm.getUsers()[i] != ""){
                    // 추가한 구성원의 정보
                    User userInfo = teamService.findByUserID(teamForm.getUsers()[i]);
                    // 그룹을 만들 때 추가된 구성원은 메인그룹이 없으면 해당 그룹이 메인그룹으로 지정된다.
                    if(userInfo.getMainTid()==null){
                        teamService.updateMainTID(userInfo.getUID(),findTeam.getTID());
                    }
                    // 그룹의 구성원이 추가되었으므로, 유저-팀테이블을 업데이트 해준다.
                    ut = new UserTeam();
                    ut.setTeam(findTeam);
                    ut.setJoinUs(true); // 생성시 추가된 구성원들은 팀에 소속된다. (JoinUs=true)
                    ut.setUser(userInfo);
                    ut.setJoinTime(LocalDateTime.now());
                    // 유저-팀 객체를 UserTeam 테이블에 저장
                    teamService.saveUserTeam(ut);
                }
            }
        }
    }

    @PostMapping("/upload")
    public String saveFile(@Login User loginUser, @RequestParam String tid, @RequestParam MultipartFile file, Model model
    ) throws IOException {
        System.out.println("upload Controller");

        if(!file.isEmpty()){
            // ========================== 파일 업로드 ========================== //
            // 업로드된 파일의 파일명을 변경 ( 중복 파일명이 될 수 있으므로, 중복되지 않을 문자로 변경해준다. )
            String fileName = renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
            String fullPath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"TeamImage"+File.separator+ fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));

            // 넘겨받은 팀아이디 정보로 TID를 찾고, TID로 팀을 찾는다.
            Team team = teamService.findTeam(teamService.findByTeamID(tid));
            // DB에 올릴 경로를 짧게 잡아준다. ( static/example.jpg )
            fullPath = File.separator + "TeamImage" + File.separator + fileName;

            // 팀 테이블에 이미지 경로 저장
            team.setTeamImage(fullPath);
            // 팀 테이블 업데이트
            teamService.saveTeam(team);
            model.addAttribute("img",fullPath);
        }

        Long curTID;
        User user = userService.findUser(loginUser.getUID());
        if(user.getMainTid() == null){
            curTID = 0L;
        }else{
            if(user.getCurTid() == null)
                curTID = user.getMainTid();
            else
                curTID = user.getCurTid();
        }
        return "redirect:/loginHome/"+curTID;
    }

    // 기능 _ 파일 업로드시 파일명 재정의하는 메서드 구현
    public String renameFiles(MultipartFile multipartFile) throws IOException {
        // 업로드한 파일명
        String originalFilename = multipartFile.getOriginalFilename();

        // ============================= 파일명 지정 ===================================
        // 원본 파일의 확장자 분리
        int pos = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(pos+1);
        // 서버에 저장할 중복되지 않을 파일명을 지정한다. (나노세컨즈 사용)
        String nanoSec = Integer.toString(LocalDateTime.now().getNano());
        // 나노세컨즈파일명.확장자로 파일명 완성
        String storeFilename = nanoSec + "." + ext;

        return storeFilename;
    }

    // 페이지 이동 & 기능 _ 아이디로 그룹 검색하는 페이지
    @GetMapping("/SearchingTeam")
    public String SearchingTeam(@Login User loginUser,@PageableDefault(page=0,size = 10,sort = "tID", direction = Sort.Direction.ASC) Pageable pageable, Model model, @RequestParam(required = false, defaultValue = "", name = "teamId") String teamId ) {
        System.out.println("SearchingTeam Controller");

        // 유저가 가입 및 가입 신청한 팀을 찾을 목적 ( 중복 신청을 방지하기 위함 )
        List<UserTeam> ut = teamService.findByUID(loginUser.getUID());
        // 유저가 속한 그룹 리스트
        List<String> myTeam = new ArrayList<>();
        // 유저의 그룹 가입 요청 여부
        List<Boolean> joinCheck = new ArrayList<>();

        for (int i = 0; i < ut.size(); i++) {
            myTeam.add(ut.get(i).getTeam().getTeamID());
            joinCheck.add(ut.get(i).isJoinUs());
        }

        // 유저가 속한 그룹아이디 목록
        model.addAttribute("myTeam", myTeam);
        // 유저의 그룹 가입 요청 여부
        model.addAttribute("joinCheck",joinCheck);

        // Spring Data JPA를 사용해 페이징을 구현
        Page<Team> all = teamService.findByTeamIDContaining(teamId,pageable);
        model.addAttribute("teams",all);

        int currentPage=all.getPageable().getPageNumber()+1; // 현재 페이지 넘버 _ 인덱스는 1부터니까 +1
        int startPage=Math.max(currentPage-4,1);
        int endPage=Math.min(currentPage+4,all.getTotalPages());
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        model.addAttribute("end",all.getTotalPages());

        Long curTID;
        if(loginUser.getCurTid() == null){
            curTID = 0L;
        }else{
            curTID = loginUser.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        return "/SearchingTeam";
    }

    // 기능 _ 그룹 요청하기
    @ResponseBody
    @PostMapping("/RequestTeam")
    public int RequestTeam(@RequestParam String id,@Login User loginUser) { // 요청한 그룹의 id값이 들어온다.
        System.out.println("requestTeam");

        // 요청과 동시에 유저팀 테이블에 소속된다. (JoinUs=false)
        UserTeam ut = new UserTeam();
        ut.setUser(loginUser); // 현재 계정의 user 정보 세팅
        ut.setTeam(teamService.findTeam(teamService.findByTeamID(id))); // 요청 그룹의 정보 세팅
        ut.setJoinTime(LocalDateTime.now()); // 신청 시간 세팅
        ut.setJoinUs(false);

        // 데이터 전달을 위한 변수
        int res = 0;

        // 중복에 대한 유효성 체크 ( 이미 요청되있거나, 그룹원인 상태면 테이블을 업데이트 하지 않는다. )
        int check = teamService.UserTeamIdCheck(loginUser.getUID(), teamService.findByTeamID(id));
        if (check == 0) {
            // 해당하는 리스트가 없으므로, 유저-팀 테이블 업데이트.
            teamService.saveUserTeam(ut);
            res = 1;
        }
        return res;
    }

    // 기능 _ 그룹 요청취소하기
    @ResponseBody
    @PostMapping("/RequestTeamCancel")
    public int RequestTeamCancel(@Login User LoginUser , @RequestParam String id) { // 요청한 그룹의 id값이 들어온다.
        System.out.println("RequestTeamCancel");
        // 전달받은 팀 정보와 유저정보를 통해 해당하는 내역을 삭제한다.
        Long Tid=teamService.findByTeamID(id);
        Long utid=teamService.findUTID(LoginUser.getUID(),Tid);
        int res = teamService.removeUTID(utid);
        return res;
    }

}

