package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.service.HlsService;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hls")
public class HlsController {

    private final HlsService hlsService;

    // 마스터 플레이리스트 파일을 반환합니다.
    @GetMapping("/{matchId}/playlist.m3u8")
    public ResponseEntity<InputStreamResource> getMaster(
            @PathVariable Long matchId,
            HttpServletRequest request  // HttpServletRequest 추가
    ) throws FileNotFoundException {
        File file = hlsService.getHlsFile(matchId.toString(), "index.m3u8");
        if (!file.exists()) {
            // 사용자 로깅 추가: 마스터 플레이리스트 파일이 존재하지 않을 때
            UserLogger.logRequest("e", "마스터 플레이리스트 파일 없음", "/api/hls/" + matchId + "/playlist.m3u8", "GET", "user", "File not found for matchId: " + matchId, request);
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // 사용자 로깅 추가: 마스터 플레이리스트 파일 반환
        UserLogger.logRequest("i", "마스터 플레이리스트 파일 반환", "/api/hls/" + matchId + "/playlist.m3u8", "GET", "user", "Match ID: " + matchId, request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/x-mpegURL"))
                .body(resource);
    }

    // 특정 세그먼트 파일을 반환합니다.
    @GetMapping("/{matchId}/{filename}")
    public ResponseEntity<InputStreamResource> getSegment(
            @PathVariable Long matchId,
            @PathVariable String filename,
            HttpServletRequest request  // HttpServletRequest 추가
    ) throws FileNotFoundException {
        File file = hlsService.getHlsFile(matchId.toString(), filename);
        if (!file.exists()) {
            // 사용자 로깅 추가: 세그먼트 파일이 존재하지 않을 때
            UserLogger.logRequest("e", "세그먼트 파일 없음", "/api/hls/" + matchId + "/" + filename, "GET", "user", "File not found for matchId: " + matchId + ", Filename: " + filename, request);
            return ResponseEntity.notFound().build();
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        // 사용자 로깅 추가: 세그먼트 파일 반환
        UserLogger.logRequest("i", "세그먼트 파일 반환", "/api/hls/" + matchId + "/" + filename, "GET", "user", "Match ID: " + matchId + ", Filename: " + filename, request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("video/MP2T"))
                .body(resource);
    }
}
