package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.dto.MatchTeamDTO;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.MatchTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가

@RestController
@RequestMapping("/api/matchTeams")
public class MatchTeamController {

    private final MatchTeamService matchTeamService;

    @Autowired
    public MatchTeamController(MatchTeamService matchTeamService) {
        this.matchTeamService = matchTeamService;
    }

    @GetMapping("/{matchId}")
    public MatchTeamDTO getMatchRecords(@PathVariable Long matchId, HttpServletRequest request) {
        MatchTeamDTO matchTeam = matchTeamService.getMatchRecords(matchId);

        // 사용자 로깅 추가: 특정 경기의 팀 기록 조회
        UserLogger.logRequest("i", "특정 경기의 팀 기록 조회", "/api/matchTeams/" + matchId, "GET", "user", "Match ID: " + matchId, request);

        return matchTeam;
    }
}
