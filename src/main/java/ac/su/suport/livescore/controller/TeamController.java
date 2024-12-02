package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.dto.TeamDTO;
import ac.su.suport.livescore.dto.TeamDTO.Request;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // 모든 팀 조회
    @GetMapping
    public ResponseEntity<List<TeamDTO.Response>> getAllTeams(HttpServletRequest request) {
        List<TeamDTO.Response> teams = teamService.getAllTeams();

        // 사용자 로깅 추가: 모든 팀 조회
        UserLogger.logRequest("i", "모든 팀 조회", "/api/teams", "GET", "user", "Teams Count: " + teams.size(), request);

        return new ResponseEntity<>(teams, HttpStatus.OK);
    }

    // 특정 팀 조회
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO.Response> getTeamById(@PathVariable Long id, HttpServletRequest request) {
        Optional<TeamDTO.Response> teamDTO = teamService.getTeamById(id);
        if (teamDTO.isPresent()) {
            // 사용자 로깅 추가: 특정 팀 조회 성공
            UserLogger.logRequest("i", "특정 팀 조회", "/api/teams/" + id, "GET", "user", "Team ID: " + id, request);
            return ResponseEntity.ok(teamDTO.get());
        } else {
            // 사용자 로깅 추가: 특정 팀 조회 실패
            UserLogger.logRequest("e", "특정 팀 조회 실패", "/api/teams/" + id, "GET", "user", "Team ID: " + id, request);
            return ResponseEntity.notFound().build();
        }
    }

    // 새로운 팀 생성
    @PostMapping
    public ResponseEntity<Request> createTeam(@RequestBody Request teamDTO, HttpServletRequest request) {
        Request createdTeam = teamService.createTeam(teamDTO);
        // 관리자 로깅 추가: 새로운 팀 생성
        AdminLogger.logRequest("i", "새로운 팀 생성", "/api/teams", "POST", "admin", "Created Team: " + createdTeam, request);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    // 팀 수정
    @PutMapping("/{id}")
    public ResponseEntity<Request> updateTeam(@PathVariable Long id, @RequestBody Request teamDTO, HttpServletRequest request) {
        Optional<Request> updatedTeam = teamService.updateTeam(id, teamDTO);
        if (updatedTeam.isPresent()) {
            // 관리자 로깅 추가: 팀 수정 성공
            AdminLogger.logRequest("i", "팀 수정", "/api/teams/" + id, "PUT", "admin", "Team ID: " + id, request);
            return new ResponseEntity<>(updatedTeam.get(), HttpStatus.OK);
        } else {
            // 관리자 로깅 추가: 팀 수정 실패
            AdminLogger.logRequest("e", "팀 수정 실패", "/api/teams/" + id, "PUT", "admin", "Team ID: " + id, request);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 팀 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id, HttpServletRequest request) {
        if (teamService.deleteTeam(id)) {
            // 관리자 로깅 추가: 팀 삭제
            AdminLogger.logRequest("o", "팀 삭제", "/api/teams/" + id, "DELETE", "admin", "Team ID: " + id, request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // 관리자 로깅 추가: 팀 삭제 실패
            AdminLogger.logRequest("e", "팀 삭제 실패", "/api/teams/" + id, "DELETE", "admin", "Team ID: " + id, request);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
