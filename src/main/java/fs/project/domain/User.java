package fs.project.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uID;

    @NotNull
    private String userID;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String nickName;

    @NotNull
    private String phoneNumber;

    @NotNull
    private LocalDate birthday;

    private String userImage;

    private String coverImage;

    private Long mainTid;

    private Long curTid;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTeam> teams = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();
}
