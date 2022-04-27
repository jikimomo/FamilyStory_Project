package fs.project;

import fs.project.domain.*;
import fs.project.repository.MainPageRepository;
import fs.project.repository.TeamRepository;
import fs.project.repository.UserRepository;
import fs.project.service.MainPageService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(true)
public class MainPageTest {

    @Autowired MainPageService mainPageService;
    @Autowired TeamRepository teamRepository;
    @Autowired UserRepository userRepository;

    // teamEvent 테스트
    @Test
    @Transactional
    public void testTeamEvent() throws Exception {
        Team team = new Team();
        team.setTeamID("team");
        team.setTeamName("우리가족");
        team.setBoss(1L);
        Long saveTID = teamRepository.saveTeam(team);

        TeamEvent teamEvent = new TeamEvent();
        teamEvent.setTeam(team);
        teamEvent.setEventName("부모님 결혼기념일");
        teamEvent.setEventDate(LocalDate.of(2022,04,27));
        teamRepository.saveTeamEvent(teamEvent);

        List<TeamEvent> teamEvents = mainPageService.findTeamEvent(saveTID);
        Assertions.assertThat(teamEvents.size()).isEqualTo(1);
    }

    // birthday 테스트
    @Test
    @Transactional
    public void testBirthday() throws Exception {
        User user1 = new User();
        user1.setUserID("iiii");
        user1.setPassword("asdf");
        user1.setName("aaaa");
        user1.setEmail("iiii@gmail.com");
        user1.setNickName("asas");
        user1.setPhoneNumber("01011111111");
        user1.setBirthday(LocalDate.of(2002,04,11));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUserID("22");
        user2.setPassword("22");
        user2.setName("22");
        user2.setEmail("22@gmail.com");
        user2.setNickName("22");
        user2.setPhoneNumber("01022222222");
        user2.setBirthday(LocalDate.of(2016,04,27));
        userRepository.save(user2);

        Team team = new Team();
        team.setTeamID("team");
        team.setTeamName("우리가족");
        team.setBoss(1L);
        Long saveTID = teamRepository.saveTeam(team);

        UserTeam userTeam1 = new UserTeam();
        userTeam1.setUser(user1);
        userTeam1.setTeam(team);
        userTeam1.setJoinUs(true);
        userTeam1.setJoinTime(LocalDateTime.now());
        teamRepository.saveUserTeam(userTeam1);

        UserTeam userTeam2 = new UserTeam();
        userTeam2.setUser(user2);
        userTeam2.setTeam(team);
        userTeam2.setJoinUs(true);
        userTeam2.setJoinTime(LocalDateTime.now());
        teamRepository.saveUserTeam(userTeam2);

        List<User> users = mainPageService.findBirthday(saveTID);
        Assertions.assertThat(users.size()).isEqualTo(1);
    }

    // newRequest 테스트
    @Test
    @Transactional
    public void testNewRequest() throws Exception {
        User user = new User();
        user.setUserID("1234");
        user.setPassword("1234");
        user.setName("1234");
        user.setEmail("1234@gmail.com");
        user.setNickName("22");
        user.setPhoneNumber("01012341234");
        user.setBirthday(LocalDate.of(2016,04,27));
        userRepository.save(user);

        User user2 = new User();
        user2.setUserID("22");
        user2.setPassword("22");
        user2.setName("22");
        user2.setEmail("22@gmail.com");
        user2.setNickName("22");
        user2.setPhoneNumber("01022222222");
        user2.setBirthday(LocalDate.of(2016,04,27));
        userRepository.save(user2);

        Team team = new Team();
        team.setTeamID("team1234");
        team.setTeamName("1234");
        team.setBoss(user.getUID());
        Long saveTID = teamRepository.saveTeam(team);

        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setJoinUs(true);
        userTeam.setJoinTime(LocalDateTime.now());
        teamRepository.saveUserTeam(userTeam);

        UserTeam userTeam2 = new UserTeam();
        userTeam2.setUser(user2);
        userTeam2.setTeam(team);
        userTeam2.setJoinUs(false);
        userTeam2.setJoinTime(LocalDateTime.now());
        teamRepository.saveUserTeam(userTeam2);

        List<User> users = mainPageService.findRequestJoinUs(user.getUID(), saveTID);
        Assertions.assertThat(users.size()).isEqualTo(1);
    }

}
