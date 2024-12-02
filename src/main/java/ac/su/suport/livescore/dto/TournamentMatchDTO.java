package ac.su.suport.livescore.dto;

import lombok.Data;

import java.util.List;

@Data
public class TournamentMatchDTO {
    private Long id;
    private String name;
    private Long nextMatchId;
    private String tournamentRoundText;
    private String startTime;
    private String state;
    private List<ParticipantDTO> participants;
}

