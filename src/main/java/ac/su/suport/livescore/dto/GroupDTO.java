package ac.su.suport.livescore.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {
    private String group;
    private List<TeamStandingDTO> teams;
}