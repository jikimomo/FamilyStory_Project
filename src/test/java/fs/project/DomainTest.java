package fs.project;

import fs.project.domain.*;
import fs.project.repository.DomainRepository;
import fs.project.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(false)
public class DomainTest {

    @Autowired
    DomainRepository domainRepository;

    @Test
    @Transactional
    public void testUser() throws Exception{
        //given
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));

        //when
        Long savedId = domainRepository.saveUser(user);
        User findUser = domainRepository.findUser(savedId);

        //then
        Assertions.assertThat(findUser.getUID()).isEqualTo(user.getUID());
        Assertions.assertThat(findUser.getUserID()).isEqualTo(user.getUserID());
        Assertions.assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
        Assertions.assertThat(findUser.getName()).isEqualTo(user.getName());
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @Transactional
    @Commit
    public void testTeam() throws Exception{

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");

        Long savedId = domainRepository.saveTeam(team);
        Team findTeam = domainRepository.findOneTeam(savedId);

        Assertions.assertThat(findTeam.getTID()).isEqualTo(team.getTID());
        Assertions.assertThat(findTeam.getTeamID()).isEqualTo(team.getTeamID());
        Assertions.assertThat(findTeam.getTeamName()).isEqualTo(team.getTeamName());

    }

    @Test
    @Transactional
    @Commit
    public void testTeamEvent() throws Exception{
        Team team = new Team();
        team.setTeamID("asdfg");
        team.setTeamName("우리가족1");
        Long savedId11 = domainRepository.saveTeam(team);
        Team findTeam = domainRepository.findOneTeam(savedId11);

        TeamEvent teamEvent = new TeamEvent();
        teamEvent.setTeam(findTeam);
        teamEvent.setEventName("엄마 생신");
        teamEvent.setEventDate(LocalDate.of(2022,04,11));

        Long savedId = domainRepository.saveTeamEvent(teamEvent);
        TeamEvent findTeamEvent = domainRepository.findTeamEvent(savedId);

        Assertions.assertThat(findTeamEvent.getTeID()).isEqualTo(teamEvent.getTeID());
        Assertions.assertThat(findTeamEvent.getTeam()).isEqualTo(teamEvent.getTeam());
        Assertions.assertThat(findTeamEvent.getEventName()).isEqualTo(teamEvent.getEventName());
    }

    @Test
    @Transactional
    public void testContent() throws Exception{
        //given
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));

        Long savedId = domainRepository.saveUser(user);
        User findUser = domainRepository.findUser(savedId);

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");

        Long savedId11 = domainRepository.saveTeam(team);
        Team findTeam = domainRepository.findOneTeam(savedId11);

        Content content = new Content();
        content.setUser(user);
        content.setTeam(team);
        content.setPhotoRoute("/images/1.jpg");
        content.setExplanation("가족 여행");
        content.setLocation("서울대공원");
        content.setWhen(LocalDate.of(2022,04,11));
        content.setUploadTime(LocalDateTime.now());

        Long savedIdContent = domainRepository.saveContent(content);
        Content findContent = domainRepository.findContent(savedIdContent);

        Assertions.assertThat(findContent.getCID()).isEqualTo(content.getCID());
        Assertions.assertThat(findContent.getTeam()).isEqualTo(content.getTeam());
        Assertions.assertThat(findContent.getUser()).isEqualTo(content.getUser());
        Assertions.assertThat(findContent.getPhotoRoute()).isEqualTo(content.getPhotoRoute());
    }

    @Test
    @Transactional
    public void testUserTeam() throws Exception{
        //given
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));

        Long savedId = domainRepository.saveUser(user);
        User findUser = domainRepository.findUser(savedId);

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");

        Long savedId11 = domainRepository.saveTeam(team);
        Team findTeam = domainRepository.findOneTeam(savedId11);

        UserTeam userTeam = new UserTeam();
        userTeam.setUser(user);
        userTeam.setTeam(team);
        userTeam.setJoinTime(LocalDateTime.now());

        Long savedIdUserTeam = domainRepository.saveUserTeam(userTeam);
        UserTeam findUserTeam = domainRepository.findUserTeam(savedIdUserTeam);

        Assertions.assertThat(findUserTeam.getUtID()).isEqualTo(userTeam.getUtID());
        Assertions.assertThat(findUserTeam.getUser()).isEqualTo(userTeam.getUser());
        Assertions.assertThat(findUserTeam.getTeam()).isEqualTo(userTeam.getTeam());
    }
}
