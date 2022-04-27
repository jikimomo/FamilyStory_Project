package fs.project.repository;

import fs.project.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContentRepository {

    private final EntityManager em;

    /* content repository */
    //content를 insert하는 메서드
    public void save(Content content){
        em.persist(content);
    }

    //cID에 해당하는 content를 select 하는 메서드
    public Content findOne(Long id){
        return em.find(Content.class, id);
    }

    //tID가 일치하는 경우의 content를 select
    public List<Content> findAllByT(Long tID){
        return em.createQuery("select c from Content c join c.team t where t.tID = :tID", Content.class)
                .setParameter("tID", tID)
                .setMaxResults(1000)
                .getResultList();
    }

    //uID, tID가 일치하는 경우의 content를 select
    public List<Content> findAllByUT(Long uID, Long tID){
        return em.createQuery("select c from Content c join c.user u join c.team t where u.uID = :uID and t.tID = :tID", Content.class)
                .setParameter("uID", uID)
                .setParameter("tID", tID)
                .setMaxResults(1000)
                .getResultList();
    }

    //cID 값에 해당하는 content를 삭제하는 메서드
    public void delete(Long id){
        em.createQuery("delete from Content c where c.cID = :cID")
                .setParameter("cID", id)
                .executeUpdate();
    }

    //when에 해당하는 날짜만 select하는 메서드
    public List<String> findTid(LocalDate when, Long tid) {
        return em.createQuery("select c.photoRoute from Content c where c.when = :when and c.team.tID = :tid")
                .setParameter("when", when)
                .setParameter("tid", tid)
                .getResultList();
    }
}
