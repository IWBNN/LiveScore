package ac.su.suport.livescore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LineupDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String teamName;
        private String department;
        private String playerName;
        private String position;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long lineupId;
        private String teamName;
        private String department;
        private String playerName;
        private String position;
    }
}