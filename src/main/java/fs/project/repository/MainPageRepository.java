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

    /* 알림을 위한 repository */

    //'team event' 에서 오늘 날짜에 해당되는 이벤트가 존재한다면 select 하는 메서드
    //현재 해당하는 team의 tID 값으로 select
    public List<TeamEvent> findTeamEvent(Long tID){
        return em.createQuery("select te from TeamEvent te join te.team t where t.tID = :tID", TeamEvent.class)
                .setParameter("tID", tID)
                .getResultList();
    }

    //현재 팀에 해당하는 다른 가족들을 알고 싶거나
    //가입 요청이 들어오는 경우를 확인하기 위해 'user team' 정보를 리턴하는 메서드
    //현재 해당하는 team의 tID 값으로 select
    public List<UserTeam> findUserTeamByT(Long tID){
        return em.createQuery("select ut from UserTeam ut join ut.team t where t.tID = :tID", UserTeam.class)
                .setParameter("tID", tID)
                .getResultList();
    }


    /* main page에서 그룹 별 페이지 이동을 위한 repository */
    //uID를 통해 현재 유저가 속한 그룹을 반환하는 메서드
    public List<UserTeam> findUserTeamByU(Long uID){
        return em.createQuery("select ut from UserTeam ut join ut.user u where u.uID = :uID", UserTeam.class)
                .setParameter("uID", uID)
                .getResultList();
    }

}
