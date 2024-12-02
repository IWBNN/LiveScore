package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.service.ConvertService;
import ac.su.suport.livescore.logger.AdminLogger; // AdminLogger 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/convert")
@CrossOrigin(origins = "*") // CORS 설정 추가
public class ConvertController {

    private final ConvertService convertService;

    @PostMapping("/hls/{date}/{matchId}/{filename}")
    public ResponseEntity<String> convertToHls(
            @PathVariable String date,
            @PathVariable Long matchId,
            @PathVariable String filename,
            HttpServletRequest request
    ) {
        try {
            convertService.convertToHls(date, matchId, filename);
            AdminLogger.logRequest("i", "HLS 변환 시작", "/api/convert/hls", "POST", "admin", String.format("Date: %s, Match ID: %d, Filename: %s", date, matchId, filename), request);
            return ResponseEntity.ok("Conversion to HLS started successfully");
        } catch (Exception e) {
            AdminLogger.logRequest("e", "HLS 변환 실패", "/api/convert/hls", "POST", "admin", e.getMessage(), request);
            return ResponseEntity.badRequest().body("Conversion failed: " + e.getMessage());
        }
    }
}
