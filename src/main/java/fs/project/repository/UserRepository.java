package fs.project.repository;
import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import fs.project.form.LoginForm;
import fs.project.form.UserSetForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    //user 저장.
    public void save(User user) {
        em.persist(user);
    }

    //uid로 user 찾기
    public User findUser(Long uid){
        return em.find(User.class, uid);
    }

    //user uid로 찾기.
    public User findOne(Long uid) {
        log.info("INFO");
        System.out.println("uid = " + uid);
        List<User> all = findAll();
        for (User u : all) {
            if (u.getUID().equals(uid)) {
                System.out.println("u.getUId() = " + u.getUID());
                return u;
            }
        }
        return null;
    }

    //user 전체 조회
    public List<User> findAll(){
        List<User> result = em.createQuery("select u from User u", User.class).getResultList();
        return result;
    }

    //userId로 user 찾기
    public List<User> findUserId(String userId) {
        return em.createQuery("select u from User u where u.userID = :userId", User.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<User> findByLoginId(String loginId){
        return findAll().stream()
                .filter(u -> u.getUserID().equals(loginId))
                .findFirst();
    }

    //설정_마이페이지 유저 정보 수정(이름,비밀번호,닉네임,이메일,폰넘버)
    public void updateUser(Long updateUid, User user) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@"+user.getUserImage());
        String s = "update User u " +
                "set u.password = :newPassword ," +
                "u.name=:newName,"+
                "u.nickName=:newNickName,"+
                "u.email=:newEmail,"+
                "u.phoneNumber=:newPhoneNumber, "+
                "u.userImage=:newUserImage, "+
                "u.coverImage=:newCoverImage "+
                "where u.uID = :updateUid";

        em.createQuery(s)
                .setParameter("newPassword",user.getPassword())
                .setParameter("newName",user.getName())
                .setParameter("newNickName",user.getNickName())
                .setParameter("newEmail",user.getEmail())
                .setParameter("newPhoneNumber",user.getPhoneNumber())
                .setParameter("newUserImage",user.getUserImage())
                .setParameter("newCoverImage",user.getCoverImage())
                .setParameter("updateUid", updateUid).executeUpdate();

    }

    //패스워드 수정
    public void editPassword(Long uid, String newPassword){

        String s = "update User u set u.password = :newPassword where u.uID = :uid";
        em.createQuery(s).setParameter("newPassword",newPassword).setParameter("uid", uid).executeUpdate();
    }

    public Team findTeam(Long tid){
        return em.find(Team.class, tid);
    }

    public Long findBoss(Long tid) {
        Team t = findTeam(tid);
        return t.getBoss();
    }

    public List<User> findTeamMember(Long tId) {

        List<User> result = em.createQuery("select ut.user from UserTeam ut where ut.team.tID=:tid")
                .setParameter("tid",tId).getResultList();
        return result;
    }

    public User findTeamBoss(Long tId) {

        Team team= em.find(Team.class, tId);
        return em.find(User.class, team.getBoss());

    }

    public List<User> waitMember(Long tId){
        List<User> user = em.createQuery("select ut.user from UserTeam ut where ut.joinUs=false and ut.team.tID=:tid").setParameter("tid",tId).getResultList();
        return user;
    }

    public List<User> attendMember(Long tId) {

        List<User> user = em.createQuery("select ut.user from UserTeam ut where ut.joinUs=true and ut.team.tID=:tid")
                .setParameter("tid",tId).getResultList();
        return user;
    }

    //회원탈퇴
    public void deleteUser(Long uid) {

        em.createQuery("delete from Content c where c.user.uID = :uid").setParameter("uid", uid).executeUpdate();
        em.createQuery("delete from UserTeam ut where ut.user.uID =:uid").setParameter("uid", uid).executeUpdate();
        em.createQuery( "delete from User u where u.uID = :uid").setParameter("uid", uid).executeUpdate();

        // team에서 현재 삭제한 유저의 uid 값이 boss인애가 있다면 tid값을 도출한다.
        // tid값을 가지고 user_team에 tid값을 둘러보는데 tid가 존재한다면 user_team 테이블의 첫번째 값을 team의 boss로 등록한다. (uid)
        // 만약 tid가 존재하지 않는다면 team에서 boss가 uid인 것을 지운다.

        List<Team> team = em.createQuery("select t from Team t where t.boss=:uid").setParameter("uid", uid).getResultList();
        for(Team t : team){
            List<Long> bossuid =  em.createQuery("select ut.user.uID from UserTeam ut where ut.team.tID = :tid").setParameter("tid", t.getTID()).getResultList();
            if(bossuid.isEmpty()){
                //테이블 지우기
                em.createQuery("delete from Team t where t.tID =:tid").setParameter("tid", t.getTID()).executeUpdate();

            }
            else{
                for(Long changeBossUid : bossuid){
                    log.info("{}, {}------------", t.getTID(), changeBossUid);
                    em.createQuery("update Team t set t.boss = :changeBossUid where t.tID = :tid ")
                            .setParameter("changeBossUid", changeBossUid)
                            .setParameter("tid", t.getTID()).executeUpdate();
                    break;
                }
            }
        }
    }

}


