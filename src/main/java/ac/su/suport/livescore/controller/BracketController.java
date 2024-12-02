package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.dto.BracketDTO;
import ac.su.suport.livescore.dto.GroupDTO;
import ac.su.suport.livescore.dto.TournamentMatchDTO;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.BracketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/brackets")
public class BracketController {

    private final BracketService bracketService;

    // 특정 스포츠의 리그 브래킷 데이터를 조회합니다.
    @GetMapping("/league/{sport}")
    public ResponseEntity<Map<String, List<GroupDTO>>> getSportLeagueBrackets(@PathVariable("sport") String sport, HttpServletRequest request) {
        Map<String, List<GroupDTO>> leagueData = bracketService.getSportLeagueBrackets(sport);

        // 사용자 로깅 추가: 리그 브래킷 데이터 조회
        UserLogger.logRequest("i", "리그 브래킷 조회", "/api/brackets/league/" + sport, "GET", "user", "Sport: " + sport, request);

        return new ResponseEntity<>(leagueData, HttpStatus.OK);
    }

    // 특정 스포츠의 토너먼트 브래킷 데이터를 조회합니다.
    @GetMapping("/tournament/{sport}")
  
    public ResponseEntity<List<TournamentMatchDTO>> getSportTournamentBrackets(@PathVariable String sport) {
        log.info("Fetching tournament brackets for sport: {}", sport);
        List<TournamentMatchDTO> tournamentData = bracketService.getSportTournamentBrackets(sport);
        log.info("Returned {} tournament matches for sport: {}", tournamentData.size(), sport);
        return new ResponseEntity<>(tournamentData, HttpStatus.OK);
    }

    // 새로운 리그 브래킷을 생성합니다.
    @PostMapping("/league")
    public ResponseEntity<BracketDTO> createLeagueBracket(@RequestBody BracketDTO bracketDTO, HttpServletRequest request) {
        try {
            BracketDTO createdBracket = bracketService.createLeagueBracket(bracketDTO);

            // 관리자 로깅 추가: 리그 브래킷 생성
            AdminLogger.logRequest("i", "리그 브래킷 생성", "/api/brackets/league", "POST", "admin", createdBracket.toString(), request);

            return new ResponseEntity<>(createdBracket, HttpStatus.CREATED);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "리그 브래킷 생성 실패", "/api/brackets/league", "POST", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 기존의 리그 브래킷을 업데이트합니다.
    @PutMapping("/league/{id}")
    public ResponseEntity<BracketDTO> updateLeagueBracket(@PathVariable Long id, @RequestBody BracketDTO bracketDTO, HttpServletRequest request) {
        try {
            BracketDTO updatedBracket = bracketService.updateLeagueBracket(id, bracketDTO);
            AdminLogger.logRequest("i", "리그 브래킷 수정", "/api/brackets/league/" + id, "PUT", "admin", updatedBracket.toString(), request);
            return new ResponseEntity<>(updatedBracket, HttpStatus.OK);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "리그 브래킷 수정 실패", "/api/brackets/league/" + id, "PUT", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 기존의 리그 브래킷을 삭제합니다.
    @DeleteMapping("/league/{id}")
    public ResponseEntity<Void> deleteLeagueBracket(@PathVariable Long id, HttpServletRequest request) {
        try {
            bracketService.deleteLeagueBracket(id);
            AdminLogger.logRequest("o", "리그 브래킷 삭제", "/api/brackets/league/" + id, "DELETE", "admin", id.toString(), request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "리그 브래킷 삭제 실패", "/api/brackets/league/" + id, "DELETE", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 새로운 토너먼트 브래킷을 생성합니다.
    @PostMapping("/tournament")
    public ResponseEntity<BracketDTO> createTournamentBracket(@RequestBody BracketDTO bracketDTO, HttpServletRequest request) {
        try {
            BracketDTO createdBracket = bracketService.createTournamentBracket(bracketDTO);
            AdminLogger.logRequest("i", "토너먼트 브래킷 생성", "/api/brackets/tournament", "POST", "admin", createdBracket.toString(), request);
            return new ResponseEntity<>(createdBracket, HttpStatus.CREATED);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "토너먼트 브래킷 생성 실패", "/api/brackets/tournament", "POST", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 기존의 토너먼트 브래킷을 업데이트합니다.
    @PutMapping("/tournament/{id}")
    public ResponseEntity<BracketDTO> updateTournamentBracket(@PathVariable Long id, @RequestBody BracketDTO bracketDTO, HttpServletRequest request) {
        try {
            BracketDTO updatedBracket = bracketService.updateTournamentBracket(id, bracketDTO);
            AdminLogger.logRequest("i", "토너먼트 브래킷 수정", "/api/brackets/tournament/" + id, "PUT", "admin", updatedBracket.toString(), request);
            return new ResponseEntity<>(updatedBracket, HttpStatus.OK);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "토너먼트 브래킷 수정 실패", "/api/brackets/tournament/" + id, "PUT", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 기존의 토너먼트 브래킷을 삭제합니다.
    @DeleteMapping("/tournament/{id}")
    public ResponseEntity<Void> deleteTournamentBracket(@PathVariable Long id, HttpServletRequest request) {
        try {
            bracketService.deleteTournamentBracket(id);
            AdminLogger.logRequest("o", "토너먼트 브래킷 삭제", "/api/brackets/tournament/" + id, "DELETE", "admin", id.toString(), request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            AdminLogger.logRequest("e", "토너먼트 브래킷 삭제 실패", "/api/brackets/tournament/" + id, "DELETE", "admin", e.getMessage(), request);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 특정 스포츠와 그룹에 해당하는 리그 브래킷 데이터를 조회합니다.
    @GetMapping("/league/{sport}/{group}")
    public ResponseEntity<List<GroupDTO>> getLeagueBracketsByGroup(@PathVariable String sport, @PathVariable String group, HttpServletRequest request) {
        Map<String, List<GroupDTO>> allGroups = bracketService.getSportLeagueBrackets(sport);
        List<GroupDTO> groupData = allGroups.get(group);

        // 사용자 로깅 추가: 그룹별 리그 브래킷 데이터 조회
        UserLogger.logRequest("i", "그룹별 리그 브래킷 조회", "/api/brackets/league/" + sport + "/" + group, "GET", "user", "Sport: " + sport + ", Group: " + group, request);

        if (groupData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groupData);
    }
}
