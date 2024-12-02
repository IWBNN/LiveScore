package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.dto.LineupDTO;
import ac.su.suport.livescore.dto.LineupGroupDTO;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.LineupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lineup")
public class LineupController {

    private final LineupService lineupService;

    public LineupController(LineupService lineupService) {
        this.lineupService = lineupService;
    }

    // 팀 라인업 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getLineup(
            @PathVariable Long teamId,
            @RequestParam(name = "format", required = false, defaultValue = "flat") String format,
            HttpServletRequest request
    ) {
        if ("grouped".equals(format)) {
            LineupGroupDTO.Response lineupGroup = lineupService.getLineupGroupedFormat(teamId);
            // 사용자 로깅 추가: 그룹 형식으로 라인업 조회
            UserLogger.logRequest("i", "그룹 형식 팀 라인업 조회", "/api/lineup/" + teamId, "GET", "user", "Format: grouped, Team ID: " + teamId, request);
            return new ResponseEntity<>(lineupGroup, HttpStatus.OK);
        } else {
            List<LineupDTO.Response> lineup = lineupService.getLineupFlatFormat(teamId);
            // 사용자 로깅 추가: 평면 형식으로 라인업 조회
            UserLogger.logRequest("i", "평면 형식 팀 라인업 조회", "/api/lineup/" + teamId, "GET", "user", "Format: flat, Team ID: " + teamId, request);
            return new ResponseEntity<>(lineup, HttpStatus.OK);
        }
    }

    // 팀 라인업 생성
    @PostMapping("/{teamId}")
    public ResponseEntity<?> createLineup(
            @PathVariable Long teamId,
            @RequestBody List<LineupDTO.Request> lineupDTOs,
            HttpServletRequest request
    ) {
        try {
            List<LineupDTO.Response> createdLineup = lineupService.createLineup(teamId, lineupDTOs);
            // 관리자 로깅 추가: 팀 라인업 생성
            AdminLogger.logRequest("i", "팀 라인업 생성", "/api/lineup/" + teamId, "POST", "admin", "Team ID: " + teamId, request);
            return new ResponseEntity<>(createdLineup, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // 관리자 로깅 추가: 팀 라인업 생성 실패
            AdminLogger.logRequest("e", "팀 라인업 생성 실패", "/api/lineup/" + teamId, "POST", "admin", "Team ID: " + teamId + ", Error: " + e.getMessage(), request);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 팀 라인업 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<List<LineupDTO.Response>> updateLineup(
            @PathVariable Long teamId,
            @RequestBody List<LineupDTO.Request> lineupDTOs,
            HttpServletRequest request
    ) {
        List<LineupDTO.Response> updatedLineup = lineupService.updateLineup(teamId, lineupDTOs);
        // 관리자 로깅 추가: 팀 라인업 수정
        AdminLogger.logRequest("i", "팀 라인업 수정", "/api/lineup/" + teamId, "PUT", "admin", "Team ID: " + teamId, request);
        return new ResponseEntity<>(updatedLineup, HttpStatus.OK);
    }

    // 팀 라인업 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteLineup(@PathVariable Long teamId, HttpServletRequest request) {
        lineupService.deleteLineup(teamId);
        // 관리자 로깅 추가: 팀 라인업 삭제
        AdminLogger.logRequest("o", "팀 라인업 삭제", "/api/lineup/" + teamId, "DELETE", "admin", "Team ID: " + teamId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
