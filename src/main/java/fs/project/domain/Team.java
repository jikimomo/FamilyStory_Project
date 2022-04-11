package fs.project.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Team {

    @Id
    @GeneratedValue
    private Long tID;

    @NotNull
    private String teamID;

    @NotNull
    private String teamName;

    private String teamImage;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<UserTeam> users = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamEvent> teamEvents = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();
}
