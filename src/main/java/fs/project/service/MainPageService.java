package fs.project.service;

import fs.project.domain.*;
import fs.project.repository.ContentRepository;
import fs.project.repository.MainPageRepository;
import fs.project.vo.UserVO;
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

    /* 알림을 위한 service */
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
            if(ut.isJoinUs() == true) { //같은 팀으로 확정된 사람들만!
                userInOneTeam.add(contentRepository.findUser(ut.getUser().getUID()));
            }
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

    //내가 보스이고 보스인 팀에 요청이 들어온다면 해당 user(즉, join_us == false)을 리턴하는 메서드
    @Transactional(readOnly = true)
    public List<User> findRequestJoinUs(Long uID, Long tID){
        List<User> newRequest = new ArrayList<>();

        if(tID != 0) {
            Team team = contentRepository.findTeam(tID);
            if (team.getBoss() == uID) { //현재 팀의 보스가 나인 경우
                List<UserTeam> userTeams = mainPageRepository.findUserTeamByT(tID);
                for (UserTeam ut : userTeams) {
                    if (!ut.isJoinUs()) {
                        User joinUsUser = contentRepository.findUser(ut.getUser().getUID());
                        newRequest.add(joinUsUser);
                    }
                }
            }
        }

        return newRequest;
    }

    /* main page service */
    //유저가 속한 그룹을 모두 반환하는 메서드
    @Transactional(readOnly = true)
    public List<Team> findCurrentTeamsByU(Long uID){
        List<Team> currentTeams = new ArrayList<>();
        List<UserTeam> userTeams = mainPageRepository.findUserTeamByU(uID);

        for(UserTeam ut: userTeams){
            if(ut.isJoinUs() == true) {
                currentTeams.add(contentRepository.findTeam(ut.getTeam().getTID()));
            }
        }

        return currentTeams;
    }

    //현재 그룹의 구성원을 반환하는 메서드
    @Transactional(readOnly = true)
    public List<User> findUserInSameTeam(Long tID){
        List<UserTeam> userTeams = mainPageRepository.findUserTeamByT(tID);
        List<User> users = new ArrayList<>();

        for(UserTeam ut : userTeams){
            if(ut.isJoinUs() == true) {
                users.add(contentRepository.findUser(ut.getUser().getUID()));
            }
        }

        return users;
    }

    //수정된 user를 update하기 위해 영속성 user로 만들어줌
    @Transactional
    public void updateUserCurID(Long uID, Long curTID){
        User user = contentRepository.findUser(uID);
        user.setCurTid(curTID);
    }

}
