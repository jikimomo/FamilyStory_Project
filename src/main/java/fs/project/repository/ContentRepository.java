package fs.project.repository;

import fs.project.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContentRepository {

    private final EntityManager em;

    /* user, team 임시 repository*/
    public User findUser(Long id){
        return em.find(User.class, id);
    }
    public Team findTeam(Long id){
        return em.find(Team.class, id);
    }

    /* content repository */
    //content를 insert하는 메서드
    public void save(Content content){
        em.persist(content);
    }

    //cID에 해당하는 content를 select 하는 메서드
    public Content findOne(Long id){
        Content content = em.find(Content.class, id);
        return content;
    }

    //모든 content 찾는 경우 -> 아마 쓸 일이 없을 듯
//    public List<Content> findAll(){
//        return em.createQuery("select c from Content c", Content.class).getResultList();
//    }

    //tID가 일치하는 경우의 content를 select -> 메인 페이지용
    public List<Content> findAllByT(Long tID){
        return em.createQuery("select c from Content c join c.team t where t.tID = :tID", Content.class)
                .setParameter("tID", tID)
                .setMaxResults(1000)
                .getResultList();
    }

    //uID, tID가 일치하는 경우의 content를 select -> 개인 페이지용
    public List<Content> findAllByUT(Long uID, Long tID){
        return em.createQuery("select c from Content c join c.user u join c.team t where u.uID = :uID and t.tID = :tID", Content.class)
                .setParameter("uID", uID)
                .setParameter("tID", tID)
                .setMaxResults(1000)
                .getResultList();
    }

    //cID 값에 해당하는 content를 삭제하는 메서드
    public void delete(Long id){
        Content content = em.find(Content.class, id);
        em.remove(content);
    }
}
