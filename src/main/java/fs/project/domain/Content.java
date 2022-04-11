package fs.project.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Content {

    @Id
    @GeneratedValue
    private Long cID;

    @NotNull
    private String photoRoute;

    @NotNull
    private String explanation;

    @NotNull
    private String location;

    @NotNull
    private LocalDate when;

    @NotNull
    private LocalDateTime uploadTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="uID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tID")
    private Team team;
}
