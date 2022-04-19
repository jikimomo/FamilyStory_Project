package fs.project.service;

import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.repository.ContentRepository;
import fs.project.repository.MainPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final ContentRepository contentRepository;
    private final MainPageRepository mainPageRepository;

    /* 알림을 위한 TeamEvent 임시 service */
    //tID를 통해서 repository에서 List<teamEvent>를 받아옴
    //현재 팀의 event중 오늘 날짜에 해당하는 event가 있다면 return하는 메서드
    @Transactional(readOnly = true)
    public List<TeamEvent> findTeamEvent(Long tID){
        List<TeamEvent> todayTeamEvent = new ArrayList<>();
        List<TeamEvent> allTeamEvent = mainPageRepository.findTeamEvent(tID);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate now = LocalDate.now();
        String nowString = now.format(formatter);

        for(TeamEvent teamEvent : allTeamEvent){
            LocalDate eventDate = teamEvent.getEventDate();
            String eventDateString = eventDate.format(formatter);

            if(nowString.equals(eventDateString)){
                todayTeamEvent.add(teamEvent);
            }
        }

        return todayTeamEvent;
    }

    /* 생일 알림을 위한 임시 service */
    //현재 속한 팀의 가족 중에서 오늘 생일인 경우를 return 하는 메서드
    //먼저 현재 팀에 속한 다른 가족들을 repository에서 받아옴
    //받아온 List<userteam>의 uid를 통해 List<user>를 받아옴
    //List<user> 중 오늘 생일인 사람을 return
    @Transactional(readOnly = true)
    public List<User> findBirthday(Long tID){
        List<User> user = new ArrayList<>();

        List<User> userInOneTeam = new ArrayList<>();
        List<UserTeam> userTeams = mainPageRepository.findUserTeamByT(tID);
        for(UserTeam ut : userTeams){
            userInOneTeam.add(contentRepository.findUser(ut.getUser().getUID()));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate now = LocalDate.now();
        String nowString = now.format(formatter);
        for(User u : userInOneTeam){
            LocalDate birthday = u.getBirthday();
            String birthdayString = birthday.format(formatter);

            if(birthdayString.equals(nowString)){
                user.add(u);
            }
        }

        return user;
    }

    /* main page service */
    @Transactional(readOnly = true)
    public List<Team> findCurrentTeamsByU(Long uID){
        List<Team> currentTeams = new ArrayList<>();
        List<UserTeam> userTeams = mainPageRepository.findUserTeamByU(uID);

        for(UserTeam ut: userTeams){
            currentTeams.add(contentRepository.findTeam(ut.getTeam().getTID()));
        }

        return currentTeams;
    }
}
