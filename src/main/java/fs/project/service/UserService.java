package fs.project.service;
import fs.project.argumentresolver.Login;
import fs.project.domain.*;
import fs.project.form.FindPwForm;
import fs.project.form.LoginForm;
import fs.project.form.UserSetForm;
import fs.project.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.io.File;
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
    private final ContentRepository contentRepository;

    //메일보내기
    @Autowired // JavaMailSender 사용 위해 Autowired 필요
    private JavaMailSender javaMailSender;//build.gradle - implementation 'org.springframework.boot:spring-boot-starter-mail'
    private static final String FROM_ADDRESS = "multicampusgroup6@gmail.com";//송신 이메일

    public User findUser(Long uid){
        return userRepository.findUser(uid);
    }


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

    //uid로 user한명만 불러오기 -> 유저 레파지포리로 반환
    public User findOne(Long uid){
        List <User> all = userRepository.findAll();
        for (User u : all) {
            if (u.getUID().equals(uid)) {
                return u;
            }
        }
        return null;
    }

    //SettingController에서 postMapping에서 updateUser를 찾아들어오고 userRepository.updateUser로 이동
    public void updateUser(Long updateUid, UserSetForm form) throws Exception {

        String userImage = filePathForUserProfileImage(form.getUserImage());
        String coverImage = filePathForUserCoverImage(form.getUserCoverImage());

        User user = new User();
        user.setPassword(form.getPassword());
        user.setName(form.getName());
        user.setNickName(form.getNickName());
        user.setEmail(form.getEmail());
        user.setPhoneNumber(form.getPhoneNumber());

        User login = userRepository.findUser(updateUid);
        if(userImage != null) {
            user.setUserImage(userImage);
        }
        else {
            user.setUserImage(login.getUserImage());
        }
        if(coverImage != null) {
            user.setCoverImage(coverImage);
        }
        else {
            user.setCoverImage(login.getCoverImage());
        }

        userRepository.updateUser(updateUid, user);
    }



    //그룹(팀)찾기
    //teamRepository의 findTeam의 userId라는 매개변수로 리던한다.
    //타입은 List형식
    public List<Team> findTeam(Long userId){
        return teamRepository.findTeam(userId);
    }

    //메인그룹(팀) 바꾸기
    //teamRepository의 changeMainTeam로 메소드로 이동해서 디비를 가져온다
    public void changeMainTeam(Long uid, Long tid) {
        teamRepository.changeMainTeam(uid, tid);
    }


    //그룹(팀) 탈퇴하기 -> 반환값이 없고 userTeamRepository의 dropTeam이라는 메소드로 이동해서 디비를 가져온다.
    public void dropTeam(Long uid, Long tid) {

        //UserTeam으로부터 삭제를 하는데 삭제를 하는 조건은 UserTeam의 tID(현재 로그인된 세션의 tID)이면서
        //UserTeam의 user의 uID(현재 로그인된 세션의 uid)인것을 삭제한다.
        // 유저팀에서 나를 삭제
        userTeamRepository.deleteUserTeam(uid, tid);


      /*user의 main_tid가삭제할 user_team 의 tid와 같다면 user가 속한 uid값을 들고 있는
        user_team의 uid가 일치하는 값이 하나라도 존재한다면 그걸 main_tid로 둔다.
        */
        User curUser = userTeamRepository.findUser(uid);
        if(curUser.getMainTid()==tid){ //
            //List<UserTeam> mainTeamChange = userTeamRepository.findAll();
            List<UserTeam> mainTeamChange = userTeamRepository.findmainTeam();
            boolean check =false;
            //join_us == true인 모든 팀 중에서
            for (UserTeam mtc : mainTeamChange) {
                //내가 포함된 팀이 있다면
                if (mtc.getUser().getUID()==uid) {

                    //main_tid를 update해준다.
                    userTeamRepository.updateMainTID(mtc.getTeam().getTID(), uid);

                    //cur_tid가 현재 탈퇴하고 싶은 그룹이라면 main_tid로 바꿔줘야함
                    if(curUser.getCurTid() == tid){
                        userTeamRepository.updateCurTID(mtc.getTeam().getTID(), uid);
                    }

                    check=true;
                    break;
                }
            }
            if(check==false){
                // 다른팀이 없으면 내 메인팀은 널이야. Team의 boss를 찾은 uid 값을 넣는다.
                userTeamRepository.updateMainTIDNull(null, uid);
                userTeamRepository.updateCurTIDNull(null, uid);
            }
        }
        if(curUser.getCurTid()==tid){
            User tmpUser = userTeamRepository.findUser(uid);
            if(tmpUser.getMainTid() == null){
                userTeamRepository.updateCurTIDNull(null, uid);
            }
            else{
                userTeamRepository.updateCurTID(tmpUser.getMainTid(), uid);
            }
        }

        //탈퇴할 팀에 현재 user가 올린 게시글이 있다면 삭제되야함
        List<Content> contents = contentRepository.findAllByUT(uid, tid);
        for(Content content : contents){
            contentRepository.delete(content.getCID());
        }

        List<UserTeam> all = userTeamRepository.findAll(); //all 이라는 객체에 리스트 형식으로 유저 전체를 찾아서 치환

        for (UserTeam ut : all) {
            if (ut.getTeam().getTID().equals(tid)&&ut.isJoinUs()==true) { // 현재 가입요청을 제외한 팀 사람들만!
                //유저팀의 팀의 사람의 TID와 로그인된 TID가 같으면서 joinus가 트루라면
                //Team의 boss를 찾은 uid 값을 넣는다.
                //uid라는 이름을 가진 곳에(위의 :uid) UserTeam테이블의 user의 UID와 그룹원의 tid에 업데이트 실행한다.
                userTeamRepository.updateTeam(ut.getUser().getUID(), tid);
                return;
            }
        }
        List<UserTeam> userTeam = userTeamRepository.findAll();
        for (UserTeam ut : userTeam) {
            //만약 userteam의 tid와 tid의 값이 같으면서 userteam의 join_us의 값이 false 라면(그룹생성이 안되어 있다면),
            if (ut.getTeam().getTID().equals(tid)&&ut.isJoinUs()==false) {
                //Team의 boss를 찾은 uid 값을 넣는다.
                userTeamRepository.deleteUserT(tid);
            }
        }

        userTeamRepository.deleteContent(tid);
        userTeamRepository.deleteTeamEvent(tid);
        userTeamRepository.deleteTeam(tid);
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

    @Async //없으면 메일이 보내지기전까지 다음 작업 수행하지 않음 (페이지 전환 지연 방지를 위함)
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

    //회원 탈퇴
    public void deleteUser(Long uid) {

        userRepository.deleteUserTeamUid(uid);
        List<Team> team = userRepository.listTeam(uid); //boss가 탈퇴한 사용자의 팀 리스트
        for(Team t : team){
            List<Long> bossuid =  userRepository.bossUid(t.getTID());
            if(bossuid.isEmpty()){
                //테이블 지우기
                userRepository.deleteUserTeam(t.getTID());
                userRepository.deleteTeamEvent(t.getTID());
                userRepository.deleteContentUid1(t.getTID());
                userRepository.deleteTeam(t.getTID());
            }
            else{
                for(Long changeBossUid : bossuid){
                    //uid true 인거를 들고오기
                    if(userRepository.userTeamJoinUs(changeBossUid, t.getTID()).get(0).isJoinUs()){
                        userRepository.updateTeam(changeBossUid, t.getTID());
                        break;
                    }
                }
            }
        }
        userRepository.deleteUserUid(uid);

        //내가 보스가 아닌경우 팀을 탈퇴하면 content가 지워져야 함
    }

    //유저 프로필 사진
    public String filePathForUserProfileImage(List<MultipartFile> images) throws Exception{

        String userPhoto = null;

        if(!CollectionUtils.isEmpty(images)){ //이미지 파일이 존재할 경우
            //프로젝트 내의 static 폴더까지의 절대 경로
            String absolutePath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static";

            for(MultipartFile image : images){
                String originalFileExtension = new String();
                String contentType = image.getContentType();

                if(ObjectUtils.isEmpty(contentType)){ //확장자가 없는 파일 -> 처리 x
                    break;
                }
                else{
                    if(contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if(contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else  // 다른 확장자일 경우 처리 x
                        break;
                }

                String newFileName = System.nanoTime()+originalFileExtension; //이미지 이름이 겹치지 않게 나노시간을 이름으로 사진 저장
                File file = new File(absolutePath+File.separator+"userProfileImage"+File.separator+newFileName);
//                System.out.println(System.nanoTime()+" "+originalFileExtension);
//                System.out.println(newFileName);
                image.transferTo(file);
                file.setWritable(true);
                file.setReadable(true);

                userPhoto = File.separator + "userProfileImage" + File.separator + newFileName;

            }
        }

        return userPhoto;
    }

    //개인 페이지 커버 사진 업데이트
    public String filePathForUserCoverImage(List<MultipartFile> images) throws Exception{

        String userPhoto = null;

        if(!CollectionUtils.isEmpty(images)){ //이미지 파일이 존재할 경우
            //프로젝트 내의 static 폴더까지의 절대 경로
            String absolutePath = new File("").getAbsolutePath()+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static";

            for(MultipartFile image : images){
                String originalFileExtension = new String();
                String contentType = image.getContentType();

                if(ObjectUtils.isEmpty(contentType)){ //확장자가 없는 파일 -> 처리 x
                    break;
                }
                else{
                    if(contentType.contains("image/jpeg"))
                        originalFileExtension = ".jpg";
                    else if(contentType.contains("image/png"))
                        originalFileExtension = ".png";
                    else  // 다른 확장자일 경우 처리 x
                        break;
                }

                String newFileName = System.nanoTime()+originalFileExtension; //이미지 이름이 겹치지 않게 나노시간을 이름으로 사진 저장
                File file = new File(absolutePath+File.separator+"userCoverImage"+File.separator+newFileName);
//                System.out.println(System.nanoTime()+" "+originalFileExtension);
//                System.out.println(newFileName);
                image.transferTo(file);
                file.setWritable(true);
                file.setReadable(true);

                userPhoto = File.separator + "userCoverImage" + File.separator + newFileName;

            }
        }

        return userPhoto;
    }


    public List<Long> findTid(LocalDate date) {
        List <Long> tid = teamEventRepository.findTid(date);
        return tid;
    }

    public String findEvent(Long tid) {
        String eventName = "[";
        eventName+=teamEventRepository.teamName(tid)+"] 오늘은 ";
        List <String> name = teamEventRepository.findEvent(tid);
        for(String s : name)eventName+=s+", ";
        eventName = eventName.substring(0, eventName.length()-2);
        eventName+="입니다!! 축하해주세요~!   - Family Story - ";
        return eventName;
    }

    public List<String> findPhoneNumber(Long tid){
        List<String> phoneNumber = teamEventRepository.findPhoneNumber(tid);
        return phoneNumber;
    }


}
