package fs.project.controller;

import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.repository.TeamRepository;
import fs.project.repository.TeamRepository2;
import fs.project.repository.UserRepository;
import fs.project.service.TeamService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Rollback
public class TeamControllerTest {

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamRepository2 teamRepository2;
    @Autowired
    UserRepository userRepository;

    @Transactional
    @Test
//    팀 생성 테스트
    public void createTeamForm() {
        Team team = new Team();
        team.setTeamID("myTeam");
        team.setTeamName("내팀");
        team.setBoss(1l);

        Long saveTeam = teamRepository.saveTeam(team);
        Team findTeam = teamRepository.findTeam(saveTeam);

        Assertions.assertThat(findTeam.getTeamID()).isEqualTo(team.getTeamID());
        Assertions.assertThat(findTeam.getTeamName()).isEqualTo(team.getTeamName());
        Assertions.assertThat(findTeam.getBoss()).isEqualTo(team.getBoss());
    }

    @Test
    @Transactional
//    그룹 검색 테스트
    public void SearchingTeam(){
        Team team = new Team();
        team.setTeamName("a");
        team.setTeamID("a");
        team.setBoss(1l);
        teamRepository.saveTeam(team);
        Team team1 = new Team();
        team1.setTeamName("ab");
        team1.setTeamID("ab");
        team1.setBoss(1l);
        teamRepository.saveTeam(team1);
        Team team2 = new Team();
        team2.setTeamName("c");
        team2.setTeamID("c");
        team2.setBoss(1l);
        teamRepository.saveTeam(team2);

        List<Team> teamlist = new ArrayList<>();
        teamlist.add(team);
        teamlist.add(team1);

        String word = "a";
//        List<Team> teams = teamRepository.searchTeam(word);
        Pageable pageable = null;
        Page<Team> teams = teamRepository2.findByTeamIDContaining(word,pageable);

        for(int i=0; i<teams.getSize(); i++){
           Assertions.assertThat(teams.getContent().get(i).getTeamID()).isEqualTo(teamlist.get(i).getTeamID());
        }

    }

    @Test
    @Transactional
//    유저팀테이블 Joinus 저장 테스트
    public void RequestTeam(){
        Team team = new Team();
        team.setTeamName("a");
        team.setTeamID("a");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        UserTeam ut = new UserTeam();
        ut.setTeam(team);
        ut.setJoinTime(LocalDateTime.of(2022,4,26,16,33));
        ut.setJoinUs(false);
        Long userTeamUID = teamRepository.saveUserTeam(ut);
        UserTeam userTeam = teamRepository.findUserTeam(userTeamUID);
        Assertions.assertThat(userTeam.isJoinUs()).isEqualTo(ut.isJoinUs());

    }

    @Test
    @Transactional
//    유저팀테이블 삭제 테스트
    public void RequestTeamCancel(){
        Team team = new Team();
        team.setTeamName("a");
        team.setTeamID("a");
        team.setBoss(1l);
        teamRepository.saveTeam(team);

        UserTeam ut = new UserTeam();
        ut.setTeam(team);
        ut.setJoinTime(LocalDateTime.of(2022,4,26,16,33));
        ut.setJoinUs(false);
        Long userTeamUID = teamRepository.saveUserTeam(ut);

        teamRepository.removeUTID(userTeamUID);
        Assertions.assertThat(teamRepository.findByUID(userTeamUID).size()).isEqualTo(0);

    }


}