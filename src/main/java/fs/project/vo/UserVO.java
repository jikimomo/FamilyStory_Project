package fs.project.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserVO {

    private Long uID;
    private String userID;
    private String name;
    private String nickName;
    private LocalDate birthday;
    private String userImage;
    private String coverImage;
    private Long mainTid;
    private Long curTid;
}
