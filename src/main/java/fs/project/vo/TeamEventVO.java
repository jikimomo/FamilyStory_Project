package fs.project.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeamEventVO {

    private Long teID;
    private String eventName;
    private LocalDate eventDate;
}
