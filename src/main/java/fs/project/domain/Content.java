package fs.project.domain;

import fs.project.form.ContentInputVO;
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

    /* 연관 관계 메서드 */
    public void setUser(User user){
        this.user = user;
        user.getContents().add(this);
    }

    public void setTeam(Team team){
        this.team = team;
        team.getContents().add(this);
    }

    /* 생성 메서드 */
    public static Content createContent(User user, Team team, ContentInputVO contentInput){
        Content content = new Content();
        content.setUser(user);
        content.setTeam(team);
        content.setPhotoRoute(contentInput.getPhotoRoute());
        content.setExplanation(contentInput.getExplanation());
        content.setLocation(contentInput.getLocation());
        content.setWhen(contentInput.getWhen());
        content.setUploadTime(LocalDateTime.now());

        return content;
    }
}
