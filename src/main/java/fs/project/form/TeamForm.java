package fs.project.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.Date;

@Getter @Setter
public class TeamForm implements Persistable<Long> {

    @NotEmpty(message = "필수 사항입니다.")
    private String teamId;

//    @NotEmpty(message = "필수 사항입니다.")
//    private String teamName;

    private String teamImage;

    private String[] users;

    private String[] eventName;

    private String[] eventDate;

    private boolean mainTeamChecked;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
