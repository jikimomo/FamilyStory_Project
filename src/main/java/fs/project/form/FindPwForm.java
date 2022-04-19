package fs.project.form;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class FindPwForm {
    private String name;

    private String email;

    private String id;

    private String address;
    private String title;
    private String message;

}
