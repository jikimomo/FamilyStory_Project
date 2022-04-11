package fs.project.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class TeamEvent {

    @Id
    @GeneratedValue
    private Long teID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tID")
    private Team team;

    @NotNull
    private String eventName;

    @NotNull
    private LocalDate eventDate;
}
