package fs.project.repository;


import fs.project.domain.Team;
import fs.project.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Slf4j
@Repository
public class TeamRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Team> findTeam(Long userId) {

        return em.createQuery("select t from Team t, User u, UserTeam ut where ut.user.uID=u.uID and t.tID=ut.team.tID and ut.user.uID = :uId", Team.class).setParameter("uId", userId).getResultList();

    }

    public void changeMainTeam(Long uid, Long changeMainTeam) {


        String s = "update User u set u.mainTeamID = :changeMainTeam where u.uID= :uid";
        em.createQuery(s).setParameter("changeMainTeam",Long.toString(changeMainTeam)).setParameter("uid", uid).executeUpdate();

    }


}
