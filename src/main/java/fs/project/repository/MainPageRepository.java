package fs.project.repository;

import fs.project.domain.TeamEvent;
import fs.project.domain.UserTeam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MainPageRepository {

    private final EntityManager em;

    /* 알림을 위한 TeamEvent 임시 repository */
    //'team event' 에서 오늘 날짜에 해당되는 이벤트가 존재한다면 select 하는 메서드
    //현재 해당하는 team의 tID 값으로 select
    public List<TeamEvent> findTeamEvent(Long tID){
        return em.createQuery("select te from TeamEvent te join te.team t where t.tID = :tID", TeamEvent.class)
                .setParameter("tID", tID)
                .getResultList();
    }

    /* 알림을 위한 UserTeam 임시 repository */
    //'user team' 에서 현재 팀에 해당하는 다른 가족들을 select 하는 메서드
    //같은 팀에 속한 사람들의 생일을 출력하기 전에 일단 같은 팀의 사람들을 select 해야해서 구현하였음
    //현재 해당하는 team의 tID 값으로 select
    public List<UserTeam> findUserTeamByT(Long tID){
        return em.createQuery("select ut from UserTeam ut join ut.team t where t.tID = :tID", UserTeam.class)
                .setParameter("tID", tID)
                .getResultList();
    }

    /* main page를 위한 repository */
    public List<UserTeam> findUserTeamByU(Long uID){
        return em.createQuery("select ut from UserTeam ut join ut.user u where u.uID = :uID", UserTeam.class)
                .setParameter("uID", uID)
                .getResultList();
    }
}
