package fs.project.controller;

import fs.project.domain.*;
import fs.project.service.*;
import fs.project.vo.ContentVO;
import fs.project.vo.TeamEventVO;
import fs.project.vo.TeamVO;
import fs.project.vo.UserVO;
import fs.project.service.ContentService;
import fs.project.service.MainPageService;
import fs.project.argumentresolver.Login;
import fs.project.domain.User;
import fs.project.form.LoginForm;
import fs.project.service.UserService;
import fs.project.session.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Controller
@RequiredArgsConstructor
//로그인페이지인지 로그인된 홈인지 정해주는 컨트롤러
//로그인된 상태일 때 메인 팀이 정해지지 않은 상태이면 그룹 관련 페이지로 이동
//메인 팀이 정해진 상태이면 메인 페이지로 이동
public class HomeController {
    private final ContentService contentService;
    private final MainPageService mainPageService;
    private final UserService userService;
    private final TeamService teamService;

    // 최초 접근 시 해당 GetMapping을 통해서 home.html로 보여준다.
    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        // 이때, "loginForm"이라는 이름을 가진 모델에 LoginForm()의 형식을 담고 간다.
        HttpSession session = request.getSession(false);
        //현재 담겨져있는 세션값이 존재한다면 세션을 드랍한다.
        if(session !=null){
            session.invalidate();
        }

        model.addAttribute("loginForm", new LoginForm());
        return "home";
    }

    //Login 애노테이션 생성한거 적용하기.
    @GetMapping("/loginHome/{tID}")
    //세션의 정보가 들어있으면 그 내용을 User user객체에 넣어준다.
    //원래 코드는 @SessionAttribute(name = SessionConst.LOGIN_USER, required = false)
    //이건데 @Login 으로 축약시켜놨다.
    public String homeLogin(@Login User loginUser, @PathVariable("tID") Long tID, Model model, HttpServletRequest request) {

        //세션에 정보가 없으면 home.html로 보낸다.
        if (loginUser == null) {
            return "home";
        }

        Long curTID;
        User user = userService.findUser(loginUser.getUID());
        if(user.getCurTid() == null){
            curTID = user.getMainTid();
        }else{
            curTID = user.getCurTid();
        }
        model.addAttribute("curTID", curTID);

        //세션에 정보가 있다면 loginHome.html로 보낸다.

        HttpSession session = request.getSession();

        // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
        // 즉, 로그인 회원 정보를 세션에 담아놓는다.

        session.setAttribute(SessionConst.LOGIN_USER, user);
        model.addAttribute("loginUser", user);

        String access_Token = (String)session.getAttribute("access_Token");
        //카카오 로그인인지 아닌지 확인 로직
        if(access_Token != null && !"".equals(access_Token)) {
            model.addAttribute("kakaoLogin", true);
        }
        else model.addAttribute("kakaoLogin", false);

        if (user.getMainTid()==null) {
            return "AfterJoin";
        } else{
            if(curTID != 0L)
                return "redirect:/loadMainPage/" + curTID;
            else{
                return "redirect:/loadMainPage/" + user.getMainTid();
            }
        }
    }

    @GetMapping("/afterJoin")
    public String afterJoin()
    {
        return "AfterJoin";
    }

    // 단순히 메인 페이지를 띄우는 용도, 보내줘야 하는 데이터는 없음
    // 데이터(컨텐츠, 알림, 구성원 정보, 팀 정보 등)는 다른 부분에서 ajax로 넘겨줄 것임
    @GetMapping("/loadMainPage/{tID}")
    public String initMainPage(@Login User loginUser, @PathVariable("tID") Long tID, Model model){
//        Long uid = loginUser.getUID();
//        User user = contentService.findUser(uid);
//
//        Team team = contentService.findTeam(tID);
//        List<Content> contents = contentService.findAllByT(team.getTID());
//        List<TeamEvent> teamEvents = mainPageService.findTeamEvent(user.getMainTid()); // 오늘 해당되는 기념일에 관한 정보
//        List<User> userTodayBirthday = mainPageService.findBirthday(user.getMainTid()); //오늘 생일인 사람에 관한 정보
//        List<User> newRequestJoinUs = mainPageService.findRequestJoinUs(user.getUID(), user.getMainTid());
//        List<Team> myTeams = mainPageService.findCurrentTeamsByU(user.getUID()); //현재 로그인된 유저가 포함된 팀
//
//        //확인차 보내는 값
//        model.addAttribute("user", user);
//        model.addAttribute("tID", team.getTID());
//        model.addAttribute("myTeams", myTeams);
//
//        //필요한 값
//        model.addAttribute("contents", contents);
//        model.addAttribute("teamEvents", teamEvents);
//        model.addAttribute("userTodayBirthday", userTodayBirthday);
//        model.addAttribute("newRequestJoinUs", newRequestJoinUs);

//        Long curTID;
//        User user = userService.findUser(loginUser.getUID());
//        if(user.getCurTid() == null){
//            curTID = 0L;
//        }else{
//            curTID = user.getCurTid();
//        }
        model.addAttribute("curTID", tID);

        return "mainPage";
    }

//    @GetMapping("/mainPage")
//    public String mainPage(@Login User loginUser, Model model){
//
//        Long uid = loginUser.getUID();
//        User user = userService.findUser(uid);
//
//        Team team = teamService.findTeam(user.getMainTid());
//        List<Content> content = contentService.findAllByT(team.getTID());
//        List<TeamEvent> teamEvent = mainPageService.findTeamEvent(user.getMainTid()); // 오늘 해당되는 기념일에 관한 정보
//        List<User> userTodayBirthday = mainPageService.findBirthday(user.getMainTid()); //오늘 생일인 사람에 관한 정보
//        List<Team> currentTeams = mainPageService.findCurrentTeamsByU(user.getUID()); //현재 로그인된 유저가 포함된 팀
//
//        model.addAttribute("team", team);
//        model.addAttribute("contents", content);
//        model.addAttribute("teamEvent", teamEvent);
//        model.addAttribute("userTodayBirthday", userTodayBirthday);
//        model.addAttribute("currentTeams", currentTeams);
//        return "mainPage";
//    }


    //로그인 정보를 전송하는 메서드
    @ResponseBody
    @GetMapping("/initMainPage/loginUser")
    public UserVO initLoginUser(@Login User loginUser) {
        Long uid = loginUser.getUID();
        User u = userService.findUser(uid);

        UserVO userVO = new UserVO();
        userVO.setUID(u.getUID());
        userVO.setUserID(u.getUserID());
        userVO.setName(u.getName());
        userVO.setNickName(u.getNickName());
        userVO.setBirthday(u.getBirthday());
        userVO.setUserImage(u.getUserImage());
        userVO.setCoverImage(u.getCoverImage());
        userVO.setMainTid(u.getMainTid());
        if(u.getCurTid() == null){
            userVO.setCurTid(u.getMainTid());
        } else{
            userVO.setCurTid(u.getCurTid());
        }

        return userVO;
    }

    //나의 팀들을 전송하는 메서드
    @ResponseBody
    @GetMapping("/initMainPage/myTeams")
    public List<TeamVO> initMainPageForMyTeams(@Login User loginUser) {
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        List<Team> myTeams = mainPageService.findCurrentTeamsByU(user.getUID());
        List<TeamVO> myTeamVO = new ArrayList<>();
        for(Team t : myTeams){
            TeamVO tVO = new TeamVO();
            tVO.setTID(t.getTID());
            tVO.setBoss(t.getBoss());
            tVO.setTeamID(t.getTeamID());
            tVO.setTeamName(t.getTeamName());
            if(t.getTeamImage()==null){
                tVO.setTeamImage("/AdminImage/temp.png");
            }else{
                tVO.setTeamImage(t.getTeamImage());
            }
            myTeamVO.add(tVO);
        }
        return myTeamVO;
    }


    /* 메인 페이지 접근 시 메인 팀에 관한 json 데이터 전송 */
    @ResponseBody
    @PostMapping("/initMainPage/contents")
    public List<ContentVO> initMainPageForContents(@Login User loginUser, @RequestParam String tID) {
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        //db에다가 curTid를 업데이트해야 함!
        mainPageService.updateUserCurID(uid, Long.parseLong(tID));

        List<Content> contents = contentService.findAllByT(Long.parseLong(tID));
        List<ContentVO> contentVO = new ArrayList<>();
        for(int i=contents.size()-1; i>=0; i--){
            Content c = contents.get(i);
            ContentVO cVO = new ContentVO();
            cVO.setCID(c.getCID());
            cVO.setExplanation(c.getExplanation());
            cVO.setLocation(c.getLocation());
            cVO.setWhen(c.getWhen());
            cVO.setPhotoRoute(c.getPhotoRoute());
            cVO.setUploadTime(c.getUploadTime());
            cVO.setUserImage(c.getUser().getUserImage());
            cVO.setUserNickname(c.getUser().getNickName());
            cVO.setTID(c.getTeam().getTID());
            contentVO.add(cVO);
        }
        return contentVO;
    }

    @ResponseBody
    @PostMapping("/initMainPage/teamEvents")
    public List<TeamEventVO> initMainPageForTeamEvents(@Login User loginUser, @RequestParam String tID){
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        List<TeamEvent> teamEvents = mainPageService.findTeamEvent(Long.parseLong(tID));
        List<TeamEventVO> teamEventVO = new ArrayList<>();
        for(TeamEvent te : teamEvents){
            TeamEventVO teVO = new TeamEventVO();
            teVO.setTeID(te.getTeID());
            teVO.setEventName(te.getEventName());
            teVO.setEventDate(te.getEventDate());
            teamEventVO.add(teVO);
        }

        return teamEventVO;
    }

    @ResponseBody
    @PostMapping("/initMainPage/todayBirthday")
    public List<UserVO> initMainPageForTodayBirthday(@Login User loginUser, @RequestParam String tID){
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        List<User> userTodayBirthday = mainPageService.findBirthday(Long.parseLong(tID)); //오늘 생일인 사람에 관한 정보
        List<UserVO> userVOTodayBirthday = new ArrayList<>();
        for(User u : userTodayBirthday){
            UserVO userVO = new UserVO();
            userVO.setUID(u.getUID());
            userVO.setUserID(u.getUserID());
            userVO.setName(u.getName());
            userVO.setNickName(u.getNickName());
            userVO.setBirthday(u.getBirthday());
            userVO.setUserImage(u.getUserImage());
            userVO.setMainTid(u.getMainTid());
            userVOTodayBirthday.add(userVO);
        }

        return userVOTodayBirthday;
    }

    @ResponseBody
    @PostMapping("/initMainPage/newRequest")
    public List<UserVO> initMainPageForNewRequest(@Login User loginUser, @RequestParam String tID){
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        List<User> userNewRequest = mainPageService.findRequestJoinUs(uid, Long.parseLong(tID));
        List<UserVO> userVONewRequest = new ArrayList<>();
        for(User u : userNewRequest){
            UserVO userVO = new UserVO();
            userVO.setUID(u.getUID());
            userVO.setUserID(u.getUserID());
            userVO.setName(u.getName());
            userVO.setNickName(u.getNickName());
            userVO.setBirthday(u.getBirthday());
            userVO.setUserImage(u.getUserImage());
            userVO.setMainTid(u.getMainTid());
            userVONewRequest.add(userVO);
        }

        return userVONewRequest;
    }

    //메인 팀의 구성원들
    @ResponseBody
    @PostMapping("/initMainPage/userInSameTeam")
    public List<UserVO> initMainPageForUserInSameTeam(@Login User loginUser, @RequestParam String tID) {
        Long uid = loginUser.getUID();
        User user = userService.findUser(uid);

        List<User> userInSameTeam = mainPageService.findUserInSameTeam(Long.parseLong(tID));
        List<UserVO> userVOInSameTeam = new ArrayList<>();
        for(User u : userInSameTeam){
            UserVO userVO = new UserVO();
            userVO.setUID(u.getUID());
            userVO.setUserID(u.getUserID());
            userVO.setName(u.getName());
            userVO.setNickName(u.getNickName());
            userVO.setBirthday(u.getBirthday());
            if(u.getUserImage()==null){
                userVO.setUserImage("/AdminImage/temp.png");
            }else{
                userVO.setUserImage(u.getUserImage());
            }
            userVO.setCoverImage(u.getCoverImage());
            userVO.setMainTid(u.getMainTid());
            userVOInSameTeam.add(userVO);
        }
        return userVOInSameTeam;
    }

    @GetMapping("/explain")
    public String explain(){
        return "explain";
    }


}