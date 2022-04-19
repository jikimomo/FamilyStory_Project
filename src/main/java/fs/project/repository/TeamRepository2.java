package fs.project.repository;

import fs.project.domain.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// SPRING DATA JPA _ 페이징에 사용
public interface TeamRepository2 extends JpaRepository<Team,Long> {
    Page<Team> findByTeamIDContaining(String id, Pageable pageable);
}
