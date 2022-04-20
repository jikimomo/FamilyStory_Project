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
        List<Team> res = em.createQuery("SELECT t FROM Team t WHERE t.teamID =:teamId", Team.class).setParameter("teamId", teamID).getResultList();
        return res;
    }

    public List<Team> searchTeam(String teamId) {
        List<Team> result = em.createQuery("SELECT t FROM Team t WHERE t.teamID LIKE concat('%',:teamId,'%')", Team.class).setParameter("teamId", teamId).getResultList();
        return result;
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
        List<User> res = em.createQuery("select u From User u WHERE u.userID=:id", User.class).setParameter("id", id).getResultList();
        return res;
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
        List<UserTeam> res = em.createQuery("select ut from UserTeam ut where ut.user.uID=:id", UserTeam.class).setParameter("id", id).getResultList();
        return res;
    }

    public int removeUTID(long utid) {
        int res = em.createQuery("delete from UserTeam ut where ut.utID=:utid").setParameter("utid", utid).executeUpdate();
        return res;
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
        return em.createQuery("select t from Team t, User u, UserTeam ut where ut.user.uID=u.uID and t.tID=ut.team.tID and ut.user.uID = :uId", Team.class).setParameter("uId", userId).getResultList();
    }

    public void changeMainTeam(Long uid, Long tid) {
        String s = "update User u set u.mainTid = :tid where u.uID= :uid";
        em.createQuery(s).setParameter("tid", tid).setParameter("uid", uid).executeUpdate();
    }

    public void updateMainTeamID(Long uid, Long tID) {
        String s = "update User u set u.mainTid = :tID where u.uID = :uid";
        em.createQuery(s).setParameter("tID", tID).setParameter("uid", uid).executeUpdate();
    }
}

