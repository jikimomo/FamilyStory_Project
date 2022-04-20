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

    //user 전체 조회.
    public List<User> findAll(){
        List<User> result = em.createQuery("select u from User u", User.class).getResultList();
        return result;
    }

    //userId로 user 찾기.
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


    public void updateUser(Long updateUid, UserSetForm form) {
        String s = "update User u " +
                "set u.password = :newPassword ," +
                "u.name=:newName,"+
                "u.nickName=:newNickName,"+
                "u.email=:newEmail,"+
                "u.phoneNumber=:newPhoneNumber "+
                "where u.uID = :updateUid";

        em.createQuery(s)
                .setParameter("newPassword",form.getPassword())
                .setParameter("newName",form.getName())
                .setParameter("newNickName",form.getNickName())
                .setParameter("newEmail",form.getEmail())
                .setParameter("newPhoneNumber",form.getPhoneNumber())
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
}


