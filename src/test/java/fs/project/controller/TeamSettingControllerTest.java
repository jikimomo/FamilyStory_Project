package fs.project.controller;

import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.repository.TeamRepository;
import fs.project.repository.UserRepository;
import fs.project.service.TeamService;
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

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Rollback
public class TeamSettingControllerTest {

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    @Transactional
    public void setMainTeam() {
        Team team = new Team();
        team.setTeamID("myTeam");
        team.setTeamName("내팀");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        User user = new User();
        user.setUserID("gildong");
        user.setPassword("1234");
        user.setName("길동");
        user.setEmail("gd@google.com");
        user.setNickName("고길동씨");
        user.setPhoneNumber("010-0000-0000");
        user.setBirthday(LocalDate.of(1999,12,13));
        user.setMainTid(team.getTID());
        userRepository.save(user);
        User findU = userRepository.findUser(user.getUID());

        teamRepository.updateMainTID(findU.getUID(),2l);
        findU = userRepository.findUser(findU.getUID());

        Assertions.assertThat(findU.getMainTid()).isEqualTo(2l);
    }

    @Transactional
    @Test
    public void acceptMember() {
        Team team = new Team();
        team.setTeamID("myT");
        team.setTeamName("내팀");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        User user = new User();
        user.setUserID("gildong");
        user.setPassword("1234");
        user.setName("길동");
        user.setEmail("gd@google.com");
        user.setNickName("고길동씨");
        user.setPhoneNumber("010-0000-0000");
        user.setBirthday(LocalDate.of(1999,12,13));
        user.setMainTid(team.getTID());
        userRepository.save(user);

        UserTeam ut = new UserTeam();
        ut.setUser(user);
        ut.setTeam(team);
        ut.setJoinTime(LocalDateTime.of(2022,12,1,16,26));
        ut.setJoinUs(false);
        teamRepository.saveUserTeam(ut);

        ut.setJoinUs(true);
        teamRepository.saveUserTeam(ut);
        Assertions.assertThat(ut.isJoinUs()).isEqualTo(true);
    }

    @Transactional
    @Test
    public void deniedMember() {
        Team team = new Team();
        team.setTeamID("myT");
        team.setTeamName("내팀");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        User user = new User();
        user.setUserID("gildong");
        user.setPassword("1234");
        user.setName("길동");
        user.setEmail("gd@google.com");
        user.setNickName("고길동씨");
        user.setPhoneNumber("010-0000-0000");
        user.setBirthday(LocalDate.of(1999,12,13));
        user.setMainTid(team.getTID());
        userRepository.save(user);

        UserTeam ut = new UserTeam();
        ut.setUser(user);
        ut.setTeam(team);
        ut.setJoinTime(LocalDateTime.of(2022,12,1,16,26));
        ut.setJoinUs(false);
        teamRepository.saveUserTeam(ut);
        teamRepository.removeUTID(ut.getUtID());

        int utSize=teamRepository.findByUID(user.getUID()).size();
        Assertions.assertThat(utSize).isEqualTo(0);
    }

    @Transactional
    @Test
    public void groupPageEdit2() {
        Team team = new Team();
        team.setTeamID("myT");
        team.setTeamName("내팀");
        team.setBoss(1l);
        Long saveTeam = teamRepository.saveTeam(team);
        userRepository.deleteTeam(saveTeam);

        int size = teamRepository.findByTeamID(team.getTeamID()).size();
        Assertions.assertThat(size).isEqualTo(0);
    }

    @Transactional
    @Test
    public void updateTeamImage() {
        Team team = new Team();
        team.setTeamID("myT");
        team.setTeamName("내팀");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        team.setTeamImage("/img/a");
        Long tid = teamRepository.saveTeam(team);
        Team findT = teamRepository.findTeam(tid);

        Assertions.assertThat(findT.getTeamImage()).isEqualTo(team.getTeamImage());
    }
}