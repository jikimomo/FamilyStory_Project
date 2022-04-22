package fs.project.repository;

import fs.project.domain.Team;
import fs.project.domain.TeamEvent;
import fs.project.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TeamEventRepository {

    private final EntityManager em;

    public List<TeamEvent> findAll(){
        List<TeamEvent> result = em.createQuery("select te from TeamEvent te", TeamEvent.class).getResultList();
        return result;
    }

    public String findEvent(Long tid) {
        Team t =  em.find(Team.class, tid);
        String s="[";
        s+=t.getTeamName()+"] 오늘은 ";

        List<String> name = em.createQuery("select te.eventName from TeamEvent te where te.team.tID= :tid").setParameter("tid", tid).getResultList();
        for(String s1 : name)s+=s1+", ";
        s = s.substring(0, s.length()-2);
        s+="입니다!! 축하해주세요~!   - Family Story - ";
        return s;
    }

    public List<String> findPhoneNumber(Long tid){

        List<String> number = em.createQuery("select u.phoneNumber from User u, UserTeam ut where ut.team.tID = :tid and ut.user.uID = u.uID and ut.joinUs=true").setParameter("tid", tid).getResultList();
        return number;
    }

    public List<Long> findTid(LocalDate date) {

        List<Long> tid = em.createQuery("select distinct te.team.tID from TeamEvent te where te.eventDate= :date").setParameter("date", date).getResultList();
        return tid;
    }
}
