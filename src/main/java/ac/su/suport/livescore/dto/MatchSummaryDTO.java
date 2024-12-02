package ac.su.suport.livescore.dto;

import ac.su.suport.livescore.constant.MatchResult;
import ac.su.suport.livescore.constant.MatchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

public class MatchSummaryDTO {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String teamName1;
        private String teamName2;
        private String department1;
        private String department2;
        private int teamScore1;
        private int teamScore2;
        private String sport;
        private String status;
        private LocalDate date;
        private LocalTime startTime;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long matchId;
        private String teamName1;
        private String teamName2;
        private String department1;
        private String department2;
        private int teamScore1;
        private int teamScore2;
        private String sport;
        private String status;
        private LocalDate date;
        private LocalTime startTime;
        private String groupName;  //밑에 3개 추가
        private String round;
        private MatchResult result;
        private MatchType matchType;
        private String teamOneSubScores;
        private String teamTwoSubScores;

    }
}