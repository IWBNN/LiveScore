package ac.su.suport.livescore.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Data
public class UserDTO {
    private String username;
    private String nickname;
    private String password;
    private String email;
}