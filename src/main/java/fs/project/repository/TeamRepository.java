package fs.project.repository;


import fs.project.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamRepository {

    private final EntityManager em;

    // Team
    public Long saveTeam(Team team) {

        em.persist(team);
        return team.getTID();
    }

    public Team findTeams(Long id) {
        return em.find(Team.class, id);
    }

    public List<Team> findByTeamID(String teamID) {

        return em.createQuery("SELECT t FROM Team t WHERE t.teamID =:teamId", Team.class)
                .setParameter("teamId", teamID)
                .getResultList();
    }

    public List<Team> searchTeam(String teamId) {

        return em.createQuery("SELECT t FROM Team t WHERE t.teamID LIKE concat('%',:teamId,'%')", Team.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }

    // User
    public Long saveUser(User user) {

        em.persist(user);
        return user.getUID();
    }

    public User findUser(Long id) {
        return em.find(User.class, id);
    }

    // 구성원 UID 찾을 시
    public List<User> findByUserID(String id) {

        return em.createQuery("select u From User u WHERE u.userID=:id", User.class)
                .setParameter("id", id)
                .getResultList();
    }

    // Userteam
    public Long saveUserTeam(UserTeam userTeam) {

        em.persist(userTeam);
        return userTeam.getUtID();
    }

    public UserTeam findUserTeam(Long id) {
        return em.find(UserTeam.class, id);
    }

    public List<UserTeam> findByUID(Long id) {

        return em.createQuery("select ut from UserTeam ut where ut.user.uID=:id", UserTeam.class)
                .setParameter("id", id)
                .getResultList();
    }

    public int removeUTID(long utid) {

        return em.createQuery("delete from UserTeam ut where ut.utID=:utid")
                .setParameter("utid", utid)
                .executeUpdate();
    }

    //teamevent
    public Long saveTeamEvent(TeamEvent teamEvent) {

        em.persist(teamEvent);
        return teamEvent.getTeID();
    }

    public TeamEvent findTeamEvent(Long id) {
        return em.find(TeamEvent.class, id);
    }

    //content
    public Long saveContent(Content content) {

        em.persist(content);
        return content.getCID();
    }

    public Content findContent(Long id) {
        return em.find(Content.class, id);
    }

    public List<Team> findTeam(Long userId) {

        return em.createQuery("select t from Team t, User u, UserTeam ut where ut.user.uID=u.uID and t.tID=ut.team.tID and ut.user.uID = :uId")
                .setParameter("uId", userId)
                .getResultList();
    }


    public void changeMainTeam(Long uid, Long tid) {

        em.createQuery("update User u set u.mainTid = :tid where u.uID= :uid")
                .setParameter("tid", tid)
                .setParameter("uid", uid)
                .executeUpdate();
    }

    //
    public void updateMainTeamID(Long uid, Long tID) {

        em.createQuery("update User u set u.mainTid = :tID where u.uID = :uid")
                .setParameter("tID", tID)
                .setParameter("uid", uid)
                .executeUpdate();
    }

    public List<User> findBossName(Long boss){

        return em.createQuery("select u from User u where u.uID=:boss")
                .setParameter("boss",boss)
                .getResultList();
    }

    public List<UserTeam> findUserTeam(String userId, Long tId) {

        return em.createQuery("select ut from UserTeam ut where ut.user.userID=:userId and ut.team.tID =:tId")
                .setParameter("userId", userId)
                .setParameter("tId", tId)
                .getResultList();
    }

    public void updateCurTeamID(Long uid, Long tid) {
        em.createQuery("update User u set u.curTid = :tID where u.uID = :uid")
                .setParameter("tID", tid)
                .setParameter("uid", uid)
                .executeUpdate();
    }
}

