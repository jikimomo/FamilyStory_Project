package fs.project.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ContentVO {

    private Long cID;
    private String photoRoute;
    private String explanation;
    private String location;
    private LocalDate when;
    private LocalDateTime uploadTime;
    private Long uID;
    private Long tID;
}
