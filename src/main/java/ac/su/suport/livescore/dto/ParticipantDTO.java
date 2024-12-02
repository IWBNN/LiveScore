package ac.su.suport.livescore.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Data
public class ParticipantDTO {
    private String id;
    private String resultText;
    private Boolean winner;
    private String status;
    private String name;
    private String image;
    private String subScore;

}