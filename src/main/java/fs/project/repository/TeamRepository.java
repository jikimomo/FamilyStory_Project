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

    public Long saveTeam(Team team) { em.persist(team); return team.getTID(); }

    public Team findTeam(Long tid) {
        return em.find(Team.class, tid);
    }

    public List<Team> findByTeamID(String teamID) {
        return em.createQuery("SELECT t FROM Team t WHERE t.teamID =:teamId", Team.class)
                .setParameter("teamId", teamID)
                .getResultList();
    }

    public User findUser(Long id) {
        return em.find(User.class, id);
    }

    public List<User> findByUserID(String id) {
        return em.createQuery("select u From User u WHERE u.userID=:id", User.class)
                .setParameter("id", id)
                .getResultList();
    }

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

    public Long saveTeamEvent(TeamEvent teamEvent) {
        em.persist(teamEvent);
        return teamEvent.getTeID();
    }

    public List<Team> findTeams(Long userId) {
        return em.createQuery("select t from Team t, User u, UserTeam ut where ut.user.uID=u.uID and t.tID=ut.team.tID and ut.user.uID = :uId")
                .setParameter("uId", userId)
                .getResultList();
    }

    public void updateMainTID(Long uid, Long tID) {
        em.createQuery("update User u set u.mainTid = :tID where u.uID = :uid")
                .setParameter("tID", tID)
                .setParameter("uid", uid)
                .executeUpdate();
    }

    public void updateCurTID(Long uid, Long tid) {
        em.createQuery("update User u set u.curTid = :tID where u.uID = :uid")
                .setParameter("tID", tid)
                .setParameter("uid", uid)
                .executeUpdate();
    }
}

