package fs.project.service;
import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import fs.project.form.FindPwForm;
import fs.project.form.LoginForm;
import fs.project.form.UserSetForm;
import fs.project.repository.TeamEventRepository;
import fs.project.repository.TeamRepository;
import fs.project.repository.UserRepository;
import fs.project.repository.UserTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@EnableAsync
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final UserTeamRepository userTeamRepository;
    private final TeamEventRepository teamEventRepository;
    //메일보내기

    @Autowired // JavaMailSender 사용 위해 Autowired 필요
    private JavaMailSender javaMailSender;//build.gradle - implementation 'org.springframework.boot:spring-boot-starter-mail'
    private static final String FROM_ADDRESS = "multicampusgroup6@gmail.com";//송신 이메일



    @Transactional(readOnly = false)
    public Optional<User> join(User user) {
        List<User> findUsers = userRepository.findUserId(user.getUserID());
        if (findUsers.isEmpty()){
            userRepository.save(user);
            return Optional.of(user);
        }
        return null;
    }

    public User login(String loginId, String password) {
/*

        Optional<User> findUserOptional = userRepository.findByLoginId(loginId);
        User user = findUserOptional.get();
        if(user.getPassword().equals(password)){
            return user;
        }
        else return null;

        아래 코드는 위의 코드를 축약 시켜놓은 코드.
        */

        return userRepository.findByLoginId(loginId).filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
    public Optional<User> findId(User user) {

        List<User> findUsers = userRepository.findAll();
        for(User u : findUsers){
            if(user.getName().equals(u.getName())&&user.getEmail().equals(u.getEmail())) {
                return Optional.of(u);
            }
        }
        return null;
    }


    public User findOne(Long uid){
        return userRepository.findOne(uid);
    }


    public void updateUser(Long updateUid, UserSetForm form) {
        userRepository.updateUser(updateUid, form);
    }


    public List<Team> findTeam(Long userId){
        return teamRepository.findTeam(userId);
    }

    public void changeMainTeam(Long uid, Long tid) {
        teamRepository.changeMainTeam(uid, tid);
    }


    public void dropTeam(Long uid, Long tid) {

        userTeamRepository.dropUserTeam(uid,tid); // user가 속한 팀을 드랍했다.
        //근데, 현재 팀에 아무도 없다면, 그냥 팀 자체를 소멸시키자.

        boolean check = userTeamRepository.findDropTeam(tid);

        if(check==false){//team 을 그냥 지워라.

            userTeamRepository.dropTeam(tid);

        }
    }




    //비밀번호 찾기
    public Optional<User> findPw(User user) {
        List<User> findUsers = userRepository.findAll();
        for(User u : findUsers){
            //name, email, userid가 모두 일치해야한다.
            if(user.getName().equals(u.getName())&&user.getEmail().equals(u.getEmail())&&user.getUserID().equals(u.getUserID())) {
                return Optional.of(u);
            }
        }
        return null;
    }







    @Async                  //없으면 메일이 보내지기전까지 다음 작업 수행하지 않음 (페이지 전환 지연 방지를 위함)
    public void mailSend(FindPwForm findPwForm){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(findPwForm.getAddress());                     //수신자
        message.setFrom(FROM_ADDRESS);                              //FROM_ADDRESS가 발신자.
        message.setSubject(findPwForm.getTitle());                  //메일 제목
        message.setText(findPwForm.getMessage());                   //메일 내용

        javaMailSender.send(message);
    }

    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

    //패스워드 수정
    public void editPassword(Long uId, String str) {
        userRepository.editPassword(uId, str);
    }

    public Long findBoss(Long tid) {
        return userRepository.findBoss(tid);
    }

    public List<User> findTeamMember(Long tId) {
        return userRepository.findTeamMember(tId);
    }

    public User findTeamBoss(Long tId) {
        return userRepository.findTeamBoss(tId);
    }
    public List<User> waitMember(Long tId){
        return userRepository.waitMember(tId);
    }
    public List<User> attendMember(Long tId) {
        return userRepository.attendMember(tId);
    }

    public void deleteUser(Long uid) {

        userRepository.deleteUser(uid);


    }



    public List<Long> findTid(LocalDate date) {
        List <Long> tid = teamEventRepository.findTid(date);
        return tid;
    }

    public String findEvent(Long tid) {
        String eventName = teamEventRepository.findEvent(tid);
        return eventName;
    }
    public List<String> findPhoneNumber(Long tid){
        List<String> phoneNumber = teamEventRepository.findPhoneNumber(tid);
        return phoneNumber;
    }
}
