package fs.project.repository;
import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TeamEventRepository {

    private final EntityManager em;

    public List<TeamEvent> findAll(){

        return em.createQuery("select te from TeamEvent te", TeamEvent.class)
                .getResultList();
    }

    public String teamName(Long tid){

        return em.find(Team.class, tid).getTeamName();
    }

    public List <String> findEvent(Long tid) {

        return em.createQuery("select te.eventName from TeamEvent te where te.team.tID= :tid")
                .setParameter("tid", tid)
                .getResultList();
    }

    public List<String> findPhoneNumber(Long tid){

        return em.createQuery("select u.phoneNumber from User u, UserTeam ut where ut.team.tID = :tid and ut.user.uID = u.uID and ut.joinUs=true")
                .setParameter("tid", tid)
                .getResultList();
    }

    public List<Long> findTid(LocalDate date) {

        return em.createQuery("select distinct te.team.tID from TeamEvent te where te.eventDate= :date")
                .setParameter("date", date)
                .getResultList();
    }
}
