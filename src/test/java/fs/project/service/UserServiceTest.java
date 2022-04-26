package fs.project.service;
import fs.project.domain.User;
import fs.project.form.UserSetForm;
import fs.project.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    void join(){
        User user = new User();
        user.setName("bae");
        user.setUserID("1");
        user.setPassword("1");
        user.setName("배정현");
        user.setNickName("bae");
        user.setEmail("bae1004kin@naver.com");
        user.setPhoneNumber("01092812121");
        user.setMainTid(1L);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse("2022-04-06",  formatter);
        user.setBirthday(localDate);
        Optional<User> u1 = userService.join(user);
    }

    @Test // uid로 유저 객체 받아오기
    void findUser() {
        join();
        User u = userService.findUser(1L);
        assertThat("배정현").isEqualTo(u.getName());
    }

    @Test // 아이디와 비밀번호가 일치하는 객체 받아오기
    void login() {
        join();
        User u1 = userService.findUser(1L);
        User u2 = userService.login("1", "1");
        assertThat(u1.getUID()).isEqualTo(u2.getUID());
    }

    @Test // 아이디와 이메일번호가 일치하는 객체 받아오기. 카카오 로그인 연동시 필요.
    void findId() {
        join();
        Optional <User> u = userService.findId("배정현", "bae1004kin@naver.com");
        assertThat(1L).isEqualTo(u.get().getUID());
    }
    @Test //유저 개인정보 수정.
    void updateUser() throws Exception {
        join();
        UserSetForm userSetForm = new UserSetForm();
        userSetForm.setName("bae");
        userSetForm.setPassword("1");
        userSetForm.setPasswordCheck("1");
        userSetForm.setName("배정현");
        userSetForm.setNickName("bae");
        userSetForm.setEmail("bae1004kin@naver.com");
        userSetForm.setPhoneNumber("01123121");
        userSetForm.setId("2");
        userService.updateUser(1L, userSetForm);
        User user = userService.findUser(1L);
        assertThat("01123121").isEqualTo(user.getPhoneNumber());
    }

    @Test //유저 테이블에 메인페이지 변경
    void changeMainTeam() {
        join();
        userService.changeMainTeam(1L, 2L);
        User u = userService.findUser(1L);
        assertThat(2L).isEqualTo(u.getMainTid());
    }

    @Test //유저 비밀번호 찾기
    void findPw() {
        join();
        Optional<User> user = userService.findPw("배정현", "bae1004kin@naver.com", "1");
        assertThat("1").isEqualTo(user.get().getPassword());
    }

    @Test //비밀 번호 변경
    void editPassword() {
        join();
        userService.editPassword(1L, "123");
        User user = userService.findUser(1L);
        assertThat("1").isNotEqualTo(user.getPassword());
    }

    @Test //회원 삭제
    void deleteUser() {
        join();
        userService.deleteUser(1L);
        User user = userService.findUser(1L);
        User n = null;
        assertThat(user).isEqualTo(n);
    }

    @Test
    void getNameEmail() {
        join();
        User user = userService.getNameEmail("배정현", "bae1004kin@naver.com");
        assertThat(1L).isEqualTo(user.getUID());
    }


//---------------------------------team-------------------------------------

    @Test
    void findTeam() {
    }

    @Test
    void dropTeam() {
    }

    @Test
    void findBoss() {
    }
    @Test
    void findTeamBoss() {
    }


    //-------------------------team, user join

    @Test
    void findTeamMember() {

    }

    @Test
    void waitMember() {

    }

    @Test
    void attendMember() {

    }

    // ----------------------team event
    @Test
    void findTid() {

    }

    @Test
    void findEvent() {

    }

    @Test
    void findPhoneNumber() {

    }



}