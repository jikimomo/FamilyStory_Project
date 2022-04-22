package fs.project.repository;

import fs.project.domain.Team;
import fs.project.domain.User;
import fs.project.domain.UserTeam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Slf4j
@Repository
public class UserTeamRepository {

    @PersistenceContext
    private EntityManager em;

    public void deleteUserT (Long tid){

        em.createQuery("delete from UserTeam ut where ut.team.tID = :tid")
                .setParameter("tid",tid)
                .executeUpdate();
    }

    public void deleteUserTeam (Long uid, Long tid){

        em.createQuery("delete from UserTeam ut where ut.team.tID = :tid and ut.user.uID = :uid")
                .setParameter("tid",tid)
                .setParameter("uid", uid)
                .executeUpdate();
    }

    public User findUser (Long uid){

        return em.find(User.class, uid);
    }

    //user 전체 조회.
    public List<UserTeam> findAll(){

        return em.createQuery("select ut from UserTeam ut", UserTeam.class).getResultList();
    }

    public void updateBossUid(Long tid, Long uid){

        em.createQuery("update User u set u.mainTid = :tid where u.uID = :uid")
                .setParameter("tid",tid).setParameter("uid", uid)
                .executeUpdate();
    }

    public void updateBossUidNull(Long tid, Long uid){

        em.createQuery("update User u set u.mainTid = :tid where u.uID = :uid")
                .setParameter("tid",null).setParameter("uid", uid)
                .executeUpdate();
    }

    public Team findTeam(Long tid){
        return em.find(Team.class, tid);
    }

    public void deleteTeam(Long tid){
        //team테이블 삭제
        em.createQuery("delete from Team t where t.tID = :tid ")
                .setParameter("tid",tid)
                .executeUpdate();
    }

    public void updateTeam(Long uid, Long tid) {

        em.createQuery("update Team t set t.boss = :uid where t.tID = :tid")
                .setParameter("uid",uid)
                .setParameter("tid", tid).executeUpdate();
    }

    public void deleteTeamEvent(Long tid) {

        em.createQuery("delete from TeamEvent te where te.team.tID = :tid")
                .setParameter("tid", tid)
                .executeUpdate();
    }

    public void deleteContent(Long tid) {

        em.createQuery("delete from Content c where c.team.tID = :tid")
                .setParameter("tid", tid)
                .executeUpdate();
    }

    public List<UserTeam> findmainTeam() {
        return em.createQuery("select ut from UserTeam ut where ut.joinUs = true").getResultList();
    }
}
