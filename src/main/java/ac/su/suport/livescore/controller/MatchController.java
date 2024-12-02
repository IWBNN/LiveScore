package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.constant.TournamentRound;
import ac.su.suport.livescore.domain.LiveVideoStream;
import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.domain.Video;
import ac.su.suport.livescore.dto.MatchModificationDTO;
import ac.su.suport.livescore.dto.MatchSummaryDTO;
import ac.su.suport.livescore.dto.TournamentMatchDTO;
import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.BracketService;
import ac.su.suport.livescore.service.LiveVideoStreamService;
import ac.su.suport.livescore.service.MatchService;
import ac.su.suport.livescore.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final BracketService bracketService;
    private final LiveVideoStreamService liveVideoStreamService;
    private final VideoService videoService;

    // 모든 경기 데이터를 단순 리스트로 반환
    @GetMapping("/all")
    public ResponseEntity<List<MatchSummaryDTO.Response>> getAllMatches(HttpServletRequest request) {
        List<MatchSummaryDTO.Response> matches = matchService.getAllMatches();
        
        // 사용자 로깅 추가: 모든 경기 데이터 조회
        UserLogger.logRequest("i", "모든 경기 데이터 조회", "/api/matches/all", "GET", "user", "Retrieved all matches", request);
        
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/view")
    public String viewMatches(Model model, HttpServletRequest request) {
        List<Match> matches = matchService.getAllMatchesWithVideos();
        model.addAttribute("matches", matches);
        
        // 사용자 로깅 추가: 경기 뷰 조회
        UserLogger.logRequest("i", "경기 뷰 조회", "/api/matches/view", "GET", "user", "Viewed matches page", request);
        
        return "matches";
    }

    // 필터링
    @GetMapping
    public ResponseEntity<List<MatchSummaryDTO.Response>> getFilteredMatches(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("Received request - sport: {}, date: {}", sport, date);
        List<MatchSummaryDTO.Response> matches = matchService.getFilteredMatches(sport, date, null);
        log.info("Returning {} matches", matches.size());
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    // 특정 경기 데이터 반환
    @GetMapping("/{id}")
    public ResponseEntity<MatchSummaryDTO.Response> getMatchById(@PathVariable Long id, HttpServletRequest request) {
        MatchSummaryDTO.Response match = matchService.getMatchById(id);
        
        // 사용자 로깅 추가: 특정 경기 데이터 조회
        UserLogger.logRequest("i", "특정 경기 데이터 조회", "/api/matches/" + id, "GET", "user", "Match ID: " + id, request);
        
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    // 매치 생성
    @PostMapping
    public ResponseEntity<MatchModificationDTO> createMatch(@RequestBody MatchModificationDTO matchModificationDTO, HttpServletRequest request) {
        MatchModificationDTO createdMatch = matchService.createMatch(matchModificationDTO);
        
        // 관리자 로깅 추가: 매치 생성
        AdminLogger.logRequest("i", "매치 생성", "/api/matches", "POST", "admin", "Created match: " + createdMatch, request);
        
        return new ResponseEntity<>(createdMatch, HttpStatus.CREATED);
    }

    // 매치 수정
    @PutMapping("/{id}")
    public ResponseEntity<MatchModificationDTO> updateMatch(@PathVariable Long id, @RequestBody MatchModificationDTO matchModificationDTO, HttpServletRequest request) {
        MatchModificationDTO updatedMatch = matchService.updateMatch(id, matchModificationDTO);
        
        // 관리자 로깅 추가: 매치 수정
        AdminLogger.logRequest("i", "매치 수정", "/api/matches/" + id, "PUT", "admin", "Updated match ID: " + id + " with data: " + matchModificationDTO, request);
        
        return new ResponseEntity<>(updatedMatch, HttpStatus.OK);
    }

    // 매치 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id, HttpServletRequest request) {
        if (matchService.deleteMatch(id)) {
            // 관리자 로깅 추가: 매치 삭제 성공
            AdminLogger.logRequest("o", "매치 삭제 성공", "/api/matches/" + id, "DELETE", "admin", "Deleted match ID: " + id, request);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // 관리자 로깅 추가: 매치 삭제 실패
            AdminLogger.logRequest("e", "매치 삭제 실패", "/api/matches/" + id, "DELETE", "admin", "Failed to delete match ID: " + id, request);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
