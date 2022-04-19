package fs.project.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Slf4j
@Repository
public class UserTeamRepository {

    @PersistenceContext
    private EntityManager em;

    public void dropTeam(Long uid, Long dropTeam) {

        String s = "delete from UserTeam ut where ut.team.tID = :tid and ut.user.uID = :uid";

        log.info("시작");
        em.createQuery(s).setParameter("tid",dropTeam).setParameter("uid", uid).executeUpdate();
        log.info("끝");

    }
}
