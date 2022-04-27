package fs.project.content;

import fs.project.domain.*;
import fs.project.repository.TeamRepository;
import fs.project.repository.UserRepository;
import fs.project.service.ContentService;
import fs.project.vo.ContentInputVO;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(true)
public class ContentTest {

    @Autowired ContentService contentService;
    @Autowired UserRepository userRepository;
    @Autowired TeamRepository teamRepository;

    // uploadContent 테스트
    @Test
    @Transactional
    public void testUploadContent() throws Exception {
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));
        userRepository.save(user);
        User findUser = userRepository.findUser(user.getUID());

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");
        team.setBoss(findUser.getUID());
        teamRepository.saveTeam(team);
        Team findTeam = teamRepository.findOneTeam(team.getTID());

        ContentInputVO content = new ContentInputVO("/images/1.jpg", "가족 여행", "서울대공원", LocalDate.of(2022,04,11));
        Long findCID = contentService.uploadContent(findUser.getUID(), findTeam.getTID(), content);

        Content findContent = contentService.findOne(findCID);
        Assertions.assertThat(findContent.getExplanation()).isEqualTo(content.getExplanation());
    }

    // findAllByT 테스트
    @Test
    @Transactional
    public void testFindAllByT() throws Exception {
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));
        userRepository.save(user);
        User findUser = userRepository.findUser(user.getUID());

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");
        team.setBoss(findUser.getUID());
        teamRepository.saveTeam(team);
        Team findTeam = teamRepository.findOneTeam(team.getTID());

        ContentInputVO content = new ContentInputVO("/images/1.jpg", "가족 여행", "서울대공원", LocalDate.of(2022,04,11));
        contentService.uploadContent(findUser.getUID(), findTeam.getTID(), content);

        List<Content> findAllContent = contentService.findAllByT(findTeam.getTID());

        Assertions.assertThat(findAllContent.size()).isEqualTo(1);
    }

    // findAllByUT 테스트
    @Test
    @Transactional
    public void testFindAllByUT() throws Exception {
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));
        userRepository.save(user);
        User findUser = userRepository.findUser(user.getUID());

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");
        team.setBoss(findUser.getUID());
        teamRepository.saveTeam(team);
        Team findTeam = teamRepository.findOneTeam(team.getTID());

        ContentInputVO content = new ContentInputVO("/images/1.jpg", "가족 여행", "서울대공원", LocalDate.of(2022,04,11));
        contentService.uploadContent(findUser.getUID(), findTeam.getTID(), content);

        List<Content> findAllContent = contentService.findAllByUT(findUser.getUID(), findTeam.getTID());

        Assertions.assertThat(findAllContent.size()).isEqualTo(1);
    }

    // testFindWhenContent 테스트
    @Test
    @Transactional
    public void testFindWhenContent() throws Exception {
        User user = new User();
        user.setUserID("iiii");
        user.setPassword("asdf");
        user.setName("aaaa");
        user.setEmail("iiii@gmail.com");
        user.setNickName("asas");
        user.setPhoneNumber("01011111111");
        user.setBirthday(LocalDate.of(2022,04,11));
        userRepository.save(user);
        User findUser = userRepository.findUser(user.getUID());

        Team team = new Team();
        team.setTeamID("asdf");
        team.setTeamName("우리가족");
        team.setBoss(findUser.getUID());
        teamRepository.saveTeam(team);
        Team findTeam = teamRepository.findOneTeam(team.getTID());

        ContentInputVO content = new ContentInputVO("/images/1.jpg", "가족 여행", "서울대공원", LocalDate.of(2022,04,11));
        Long inputCID = contentService.uploadContent(findUser.getUID(), findTeam.getTID(), content);

        List<String> photoRoute =  contentService.findTid(content.getWhen(), findTeam.getTID());

        Assertions.assertThat(photoRoute.size()).isEqualTo(1);
    }

}
