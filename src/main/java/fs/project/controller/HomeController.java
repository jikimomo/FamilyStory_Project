package fs.project.controller;
import fs.project.domain.*;
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
import java.util.List;
@Slf4j
@Controller
@RequiredArgsConstructor
//로그인페이지인지 로그인된 홈인지 정해주는 컨트롤러
public class HomeController {
    private final ContentService contentService;
    private final MainPageService mainPageService;
    private final UserService userService;

    // 최초 접근 시 해당 GetMapping을 통해서 home.html로 보여준다.
    @GetMapping("/")
    public String home(Model model) {
        // 이때, "loginForm"이라는 이름을 가진 모델에 LoginForm()의 형식을 담고 간다.
        model.addAttribute("loginForm", new LoginForm());
        return "home";
    }


    //Login 애노테이션 생성한거 적용하기.
    @GetMapping("/loginHome")
    //세션의 정보가 들어있으면 그 내용을 User user객체에 넣어준다.
    //원래 코드는 @SessionAttribute(name = SessionConst.LOGIN_USER, required = false)
    //이건데 @Login 으로 축약시켜놨다.
    public String homeLogin(@Login User loginUser, Model model, HttpServletRequest request) {

        //세션에 정보가 없으면 home.html로 보낸다.
        if (loginUser == null) {
            return "home";
        }
        //세션에 정보가 있다면 loginHome.html로 보낸다.

        HttpSession session = request.getSession();

        // 세션에 LOGIN_USER라는 이름(SessionConst.class에 LOGIN_USER값을 "loginUser")을 가진 상자에 loginUser 객체를 담음.
        // 즉, 로그인 회원 정보를 세션에 담아놓는다.

        User user = userService.findOne(loginUser.getUID());
        session.setAttribute(SessionConst.LOGIN_USER, user);


        model.addAttribute("loginUser", user);
        if(user.getMainTid()!=null){
            log.info("------------null이 아니래 ----------");
            return "redirect:/mainPage";
        }
        else {
            log.info("------------null이래-----------");
            return "AfterJoin";
        }
    }


    @GetMapping("/mainPage")
    public String mainPage(@Login User loginUser, Model model){

        Long uid = loginUser.getUID();
        User user = contentService.findUser(uid);

        Team team = contentService.findTeam(user.getMainTid());
        List<Content> content = contentService.findAllByT(team.getTID());
        List<TeamEvent> teamEvent = mainPageService.findTeamEvent(user.getMainTid()); // 오늘 해당되는 기념일에 관한 정보
        List<User> userTodayBirthday = mainPageService.findBirthday(user.getMainTid()); //오늘 생일인 사람에 관한 정보
        List<Team> currentTeams = mainPageService.findCurrentTeamsByU(user.getUID()); //현재 로그인된 유저가 포함된 팀

        model.addAttribute("team", team);
        model.addAttribute("contents", content);
        model.addAttribute("teamEvent", teamEvent);
        model.addAttribute("userTodayBirthday", userTodayBirthday);
        model.addAttribute("currentTeams", currentTeams);
        return "mainPage";
    }








}
