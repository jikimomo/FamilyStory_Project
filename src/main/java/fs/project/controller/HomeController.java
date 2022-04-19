package fs.project.controller;

import fs.project.domain.*;
import fs.project.service.ContentService;
import fs.project.service.MainPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ContentService contentService;
    private final MainPageService mainPageService;

    @GetMapping("/mainPage")
    public String home(Model model){
        Team team = contentService.findTeam(1L);
        List<Content> content = contentService.findAllByT(team.getTID());
        List<TeamEvent> teamEvent = mainPageService.findTeamEvent(1L); // 오늘 해당되는 기념일에 관한 정보
        List<User> userTodayBirthday = mainPageService.findBirthday(1L); //오늘 생일인 사람에 관한 정보
        List<Team> currentTeams = mainPageService.findCurrentTeamsByU(1L); //현재 로그인된 유저가 포함된 팀

        model.addAttribute("team", team);
        model.addAttribute("contents", content);
        model.addAttribute("teamEvent", teamEvent);
        model.addAttribute("userTodayBirthday", userTodayBirthday);
        model.addAttribute("currentTeams", currentTeams);
        return "mainPage";
    }
}
