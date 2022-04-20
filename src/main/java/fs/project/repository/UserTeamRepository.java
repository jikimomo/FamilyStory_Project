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

    //user 전체 조회.
    public List<UserTeam> findAll(){
        List<UserTeam> result = em.createQuery("select ut from UserTeam ut", UserTeam.class).getResultList();
        return result;
    }

    public void dropUserTeam(Long uid, Long tid) {

        String s = "delete from UserTeam ut where ut.team.tID = :tid and ut.user.uID = :uid";
        em.createQuery(s).setParameter("tid",tid).setParameter("uid", uid).executeUpdate();

/*        user의 main_tid가삭제할 user_team 의 tid와 같다면 user가 속한 uid값을 들고 있는
        user_team의 uid가 일치하는 값이 하나라도 존재한다면 그걸 main_tid로 둔다.
        */
        User u = em.find(User.class, uid);
        if(u.getMainTid()==tid){
            List<UserTeam> mainTeamChange = findAll();
            boolean check =false;
            for (UserTeam mtc : mainTeamChange) {
                if (mtc.getUser().getUID()==uid) {
                    String s1 = "update User u set u.mainTid = :tid where u.uID = :uid";
                    //Team의 boss를 찾은 uid 값을 넣는다.
                    em.createQuery(s1).setParameter("tid",mtc.getTeam().getTID()).setParameter("uid", uid).executeUpdate();
                    check=true;
                    break;
                }
            }
            if(check==false){
                String s2 = "update User u set u.mainTid = :tid where u.uID = :uid";
                //Team의 boss를 찾은 uid 값을 넣는다.
                em.createQuery(s2).setParameter("tid",null).setParameter("uid", uid).executeUpdate();
            }
        }
        log.info("----------------------durldurldrlul--------------");
    }
    public Team findTeam(Long tid){
        return em.find(Team.class, tid);
    }

    public boolean findDropTeam(Long tid) {
        List<UserTeam> all = findAll();
        for (UserTeam ut : all) {
            if (ut.getTeam().getTID().equals(tid)) {
                //Team의 boss를 찾은 uid 값을 넣는다.
                String s = "update Team t set t.boss = :uid where t.tID = :tid";
                em.createQuery(s).setParameter("uid",ut.getUser().getUID()).setParameter("tid", tid).executeUpdate();
              return true; //팀 테이블 지울 필요 없어
            }
        }
        return false; //팀 테이블을 지워
    }

    public void dropTeam(Long tid) {
        String s = "delete from Team t where t.tID = :tid ";
        em.createQuery(s).setParameter("tid",tid).executeUpdate();
    }
}
