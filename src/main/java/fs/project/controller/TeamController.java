package fs.project.controller;

import fs.project.argumentresolver.Login;
import fs.project.form.TeamForm;
import fs.project.repository.TeamRepository2;
import fs.project.domain.*;
import fs.project.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final TeamRepository2 teamRepository2;

    private final TeamService teamService;

    // 페이지 이동 _ 회원가입 후 이동할 페이지
    @GetMapping("/AfterJoin")
    public String AfterJoin() {
        System.out.println("AfterJoin Page");
        return "AfterJoin";
    }

    // 페이지 이동 _ 그룹 생성 클릭 후 페이지로 이동
    @GetMapping("/CreateTeam")
    public String CreateTeam(Model model,@Login User loginUser) {
        System.out.println("CreateTeam Page");
        model.addAttribute("TeamForm", new Team());
//        ===============임시! 앞단이랑 합치면 로그인한 회원의 아이디를 넣는다.=========================
        // 구성원에 자기 자신을 넣으면 안되니까 자신의 아이디를 보내줬다.
        // 로그인 연결하면, 캐시값 끌어오면 되니까 아마도 필요없을 부분...
        model.addAttribute("user", loginUser.getUserID());
        return "/CreateTeam";
    }

    // 기능 _ 뷰에서 구성원 추가시 구성원 여부
    @ResponseBody
    @PostMapping(value = "/validateMem")
    public int validateMem(@RequestParam("sendData") String id){
        // 빈값이 들어오면 회원이 아니므로 -1 리턴
        if(id==""){
            return -1;
        }else{
            int res = teamService.UserIdCheck(id);
            return res;
        }
    }

    // 기능 _ 뷰에서 그룹ID 입력시 그룹ID 중복 여부
    @ResponseBody
    @PostMapping(value = "/validateTeam")
    public int validateTeam(@RequestParam("sendData") String id){
        // 빈값이 들어오면 그룹리스트에 없으므로 -1 리턴
        if(id==""){
            return -1;
        }
        int res = teamService.TeamIdCheck(id);
        return res;
    }

    // 기능 _ 폼 데이터 DB에 저장
    // ( 폼 데이터 : TeamName(NN), TeamId(NN), users, eventName, eventDate )
    @ResponseBody
    @PostMapping(value = "/CreateTeam")
    public void  CreateTeamForm(@Valid TeamForm teamForm,@Login User loginUser) {
        System.out.println("CreateTeam Controller");
//        ===================임시 로그인 계정 _ 앞단이랑 연결하면 유저아이디로 UID 찾아서 넣으면 될 것 같다.==================
//        User findU = teamService.findUser(1l);

        // 전달받은 데이터를 Team 테이블에 저장
        Team team = new Team();
        team.setTeamID(teamForm.getTeamId());
        team.setTeamName(teamForm.getTeamName());
        team.setBoss(loginUser.getUID());
        Long saveId = teamService.saveTeam(team);

        Team findTeam = teamService.findTeam(saveId);
        loginUser.setMainTeamID(findTeam.getTID());
        teamService.updateMainTeamID(loginUser.getUID(),findTeam.getTID());


        if (saveId != 0) {
            Team findT = teamService.findTeam(saveId);
            // 기념일 정보 저장 ( 데이터가 여러개 일 수 있어서 반복문 사용 )
            for (int i = 0; i < teamForm.getEventName().length; i++) {
                if(teamForm.getEventName()[i] != ""){
                    // 전달받은 데이터를 TeamEvent 테이블에 저장
                    TeamEvent te = new TeamEvent();
                    te.setTeam(findT);
                    te.setEventName(teamForm.getEventName()[i]);
                    // DB에서 날짜 타입이 LocalDate이기 때문에 문자열로 받은 내용을 파싱.
                    LocalDate date = LocalDate.parse(teamForm.getEventDate()[i], DateTimeFormatter.ISO_DATE);
                    te.setEventDate(date);
                    teamService.saveTeamEvent(te);
                }
            }


            // User와 Team을 조인한 UserTeam테이블 업데이트. (User(join), Team(join), joinTime, joinUs )
            UserTeam ut = new UserTeam();
            ut.setTeam(findT);
            ut.setUser(loginUser);
            ut.setJoinUs(true); // 팀을 만든 사람은 가입 허락이 필요없다.
            ut.setJoinTime(LocalDateTime.now());
            teamService.saveUserTeam(ut);


            // 구성원 저장 ( 데이터가 여러개 일 수 있어서 반복문 사용 )
            for (int i = 0; i < teamForm.getUsers().length; i++) {
                if( teamForm.getUsers()[i] != ""){
                    // 추가한 구성원의 정보
                    User userInfo = teamService.findByUserID(teamForm.getUsers()[i]).get(0);
                    // 그룹을 만들 때 추가된 구성원은 그룹에 소속됨과 동시에 해당 그룹이 구성원의 메인 그룹으로 설정된다.
                    Long userUID = teamService.saveUser(userInfo);
                    userInfo = teamService.findUser(userUID);

                    // 그룹의 구성원이 추가되었으므로, 유저-팀테이블을 업데이트 해준다.
                    ut = new UserTeam();
                    ut.setTeam(findT);
                    ut.setJoinUs(true); // 생성시 추가된 구성원들은 팀에 소속된다. (JoinUs=true)
                    ut.setUser(userInfo);
                    ut.setJoinTime(LocalDateTime.now());
                    // 유저-팀 객체를 UserTeam 테이블에 저장
                    teamService.saveUserTeam(ut);
                }
            }
        }
    }

    // 파일 업로드 경로 ( properties에 저장되어 있다.)
    @Value("${file.path}")
    private String fileDir;

    // 기능 _ 팀 그룹 생성 시 이미지 업로드 & DB 저장
    @PostMapping("/upload")
    public String saveFile(@RequestParam String tid, @RequestParam MultipartFile file, Model model
    ) throws IOException {
        System.out.println("upload Controller");
        // 로그로 파일 넘어온 내용을 체크했다.
//        log.info("multi={}",file);
        if(!file.isEmpty()){
            // ========================== 파일 업로드 ========================== //
            // 업로드된 파일의 파일명을 변경 ( 중복 파일명이 될 수 있으므로, 중복되지 않을 문자로 변경해준다. )
            String fileName = renameFiles(file);
            // 실제 업로드 될 파일의 경로를 지정해준다.
            String fullPath = fileDir + fileName;
            // 해당 경로에 파일을 업로드 한다.
            file.transferTo(new File(fullPath));

            // 넘겨받은 팀아이디 정보로 TID를 찾고, TID로 팀을 찾는다.
            Team team = teamService.findTeam(teamService.findByTeamID(tid));
            // DB에 올릴 경로를 짧게 잡아준다. ( static/example.jpg )
            String[] dir = fileDir.split("/");
            fullPath = File.separator + dir[0] + File.separator + fileName;

            // 팀 테이블에 이미지 경로 세팅 후
            team.setTeamImage(fullPath);
            // 팀 테이블 업데이트
            teamService.saveTeam(team);
            model.addAttribute("img",fullPath);
        }
        return "redirect:/";
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
    public String SearchingTeam(@Login User loginUser,@PageableDefault(page=0,size = 10,sort = "tID", direction = Sort.Direction.ASC) Pageable pageable, Model model, @RequestParam(required = false, defaultValue = "", name = "teamId") String id ) {
        System.out.println("SearchingTeam Controller");


//        ===================임시 로그인 계정 _ 앞단이랑 연결하면 유저아이디로 UID 찾아서 넣으면 될 것 같다.==================
//        User findU = teamService.findUser(1l);


        // 유저가 가입 및 가입 신청한 팀을 찾을 목적 ( 중복 신청을 방지하기 위함 )
        List<UserTeam> ut = teamService.findByUID(loginUser.getUID());
        // 유저가 속한 그룹 리스트
        List<String> myTeam = new ArrayList<>();
        // 유저의 그룹 가입 요청 여부
        List<Boolean> joinCheck = new ArrayList<>();

        for (int i = 0; i < ut.size(); i++) {
            myTeam.add(ut.get(i).getTeam().getTeamID());
            joinCheck.add(ut.get(i).isJoinUs());
            System.out.println(myTeam.get(i));
        }
//        Page<Team> all = boardRepository.findAll(pageable);
        // Spring Data JPA를 사용해 페이징을 구현
        Page<Team> all = teamRepository2.findByTeamIDContaining(id,pageable);
        model.addAttribute("teams",all);

        int currentPage=all.getPageable().getPageNumber()+1; // 현재 페이지 넘버 _ 인덱스는 1부터니까 +1
        int startPage=Math.max(currentPage-4,1);
        int endPage=Math.min(currentPage+4,all.getTotalPages());
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        // 해당하는 팀이 있는지 없는지 크기값을 전달.
        List<Team> teamsort = teamService.searchTeam(id);
        model.addAttribute("size",teamsort.size());
        // 유저가 속한 그룹아이디 목록
        model.addAttribute("myTeam", myTeam);
        // 유저의 그룹 가입 요청 여부
        model.addAttribute("joinCheck",joinCheck);

        return "/SearchingTeam";
    }

    // 기능 _ 그룹 요청하기
    @ResponseBody
    @PostMapping("/RequestTeam")
    public int RequestTeam(@RequestParam String id,@Login User loginUser) { // 요청한 그룹의 id값이 들어온다.
        System.out.println("requestTeam");

//        ===================임시 로그인 계정 _ 앞단이랑 연결하면 유저아이디로 UID 찾아서 넣으면 될 것 같다.==================
//        User findU = teamService.findUser(1l);

        // 요청과 동시에 유저팀 테이블에 소속된다.
        UserTeam ut = new UserTeam();
        ut.setUser(loginUser); // 현재 계정의 user 정보 세팅
        ut.setTeam(teamService.findTeam(teamService.findByTeamID(id))); // 요청 그룹의 정보 세팅
        ut.setJoinTime(LocalDateTime.now()); // 신청 시간 세팅
        ut.setJoinUs(false); // 아직 그룹장이 수락하지 않아서 false

        // 데이터 전달을 위한 변수
        int res = 0;

        // 중복에 대한 유효성 체크 ( 이미 요청되있거나, 그룹원인 상태면 테이블을 업데이트 하지 않는다. )
        int check = teamService.UserTeamIdCheck(loginUser.getUID(), teamService.findByTeamID(id));
        if (check == 0) {
            // 해당하는 리스트가 없으므로, 유저-팀 테이블 업데이트.
            teamService.saveUserTeam(ut);
            res = 1; // 뷰에 전달
        }
        return res;
    }

    // 기능 _ 그룹 요청취소하기
    @ResponseBody
    @PostMapping("/RequestTeamCancel")
    public int RequestTeamCancel(@RequestParam String id, @Login User loginUser) { // 요청한 그룹의 id값이 들어온다.
        System.out.println("RequestTeamCancel");

//        ===================임시 로그인 계정 _ 앞단이랑 연결하면 유저아이디로 UID 찾아서 넣으면 될 것 같다.==================
//        User findU = teamService.findUser(1l);

        // 전달받은 팀 정보와 유저정보를 통해 해당하는 내역을 삭제한다.
        Long Tid=teamService.findByTeamID(id);
        Long utid=teamService.findUTID(loginUser.getUID(),Tid);
        int res = teamService.removeUTID(utid);
        return res;
    }



    // 페이지 이동
    @GetMapping("/ManageGroup")
    public String ManageGroup(){

// 탈퇴시키려면? 굳이...시켜야할까..?ㅠㅠ
        // 내가 보스인 팀의 팀원리스트 출력 -> User-Team에서 해당 utid 날려준다. 유저의 메인팀아이디 체크해서 동일하면 날려준다.

// 요청 거절
        // 내가 보스인 팀의 팀원리스트 출력 -> User-Team에서 해당 utid 날려준다. _ 요청 취소 참고하자.

// 요청 수락
        // User-Team의 JoinUs를 true로 변경해준다. 유저의 메인팀이 NULL이면 변경해준다.

// 본인이 그룹을 탈퇴하려면? _ 그룹원
        // User-Team에서 utid날려준다. 유저의 메인팀아이디 체크해서 동일하면 날려준다.

// 그룹장이 그룹을 탈퇴한다면?
        // User-Team에서 다른 한명 Boss로 올려준다. User-Team에서 utid날린다. User에서 메인팀아이디 날린다.

// 그룹 정보 수정
        // 팀명 변경, 기념일 추가 및 삭제, 팀 사진 변경



        //        ===================임시 로그인 계정 _ 앞단이랑 연결하면 유저아이디로 UID 찾아서 넣으면 될 것 같다.==================
        User findU = teamService.findUser(1l);



        return "redirect:/";
    }
}

