package fs.project.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class TeamEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teID;

    @ManyToOne
    @JoinColumn(name = "tID")
    private Team team;

    private String eventName;

    private LocalDate eventDate;
}
