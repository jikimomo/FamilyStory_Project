package fs.project.repository;

import fs.project.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DomainRepository {

    @PersistenceContext
    private EntityManager em;

    //user
    public Long saveUser(User user){
        em.persist(user);
        return user.getUID();
    }

    public User findUser(Long id){
        return em.find(User.class, id);
    }

    //team
    public Long saveTeam(Team team){
        em.persist(team);
        return team.getTID();
    }

    public Team findTeams(Long id){
        return em.find(Team.class, id);
    }

    //userteam
    public Long saveUserTeam(UserTeam userTeam) {
        em.persist(userTeam);
        return userTeam.getUtID();
    }

    public UserTeam findUserTeam(Long id){
        return em.find(UserTeam.class, id);
    }

    //teamevent
    public Long saveTeamEvent(TeamEvent teamEvent){
        em.persist(teamEvent);
        return teamEvent.getTeID();
    }

    public TeamEvent findTeamEvent(Long id){
        return em.find(TeamEvent.class, id);
    }

    //content
    public Long saveContent(Content content){
        em.persist(content);
        return content.getCID();
    }

    public Content findContent(Long id){
        return em.find(Content.class, id);
    }



}
