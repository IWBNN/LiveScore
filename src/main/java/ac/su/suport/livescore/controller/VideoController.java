package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.domain.Video;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.S3Service;
import ac.su.suport.livescore.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
@CrossOrigin(origins = "*")
public class VideoController {

    private final VideoService videoService;
    private final S3Service s3Service;

    @GetMapping
    public ResponseEntity<List<Video>> getVideos(HttpServletRequest request) {
        List<Video> videos = videoService.getAllVideos();
        videos.sort(Comparator.comparing(Video::getDate).reversed());

        // 사용자 로깅 추가: 모든 동영상 조회
        UserLogger.logRequest("i", "모든 동영상 조회", "/api/video", "GET", "user", "Retrieved all videos", request);

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<Video>> getVideosByMatchId(@PathVariable Long matchId, HttpServletRequest request) {
        List<Video> videos = videoService.getVideosByMatchId(matchId);
        videos.sort(Comparator.comparing(Video::getDate).reversed());

        // 사용자 로깅 추가: 특정 경기의 동영상 조회
        UserLogger.logRequest("i", "특정 경기의 동영상 조회", "/api/video/match/" + matchId, "GET", "user", "Match ID: " + matchId, request);

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/stream/{matchId}/{filename}")
    public ResponseEntity<InputStreamResource> streamFromS3(@PathVariable Long matchId, @PathVariable String filename, HttpServletRequest request) {
        InputStream inputStream = s3Service.getFile(matchId, filename);

        // 사용자 로깅 추가: S3에서 동영상 스트리밍
        UserLogger.logRequest("i", "동영상 스트리밍", "/api/video/stream/" + matchId + "/" + filename, "GET", "user", "Streaming video for Match ID: " + matchId + ", Filename: " + filename, request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("video/webm"))
                .body(new InputStreamResource(inputStream));
    }

    // 동영상 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteVideo(@PathVariable Long id, HttpServletRequest request) {
        boolean success = videoService.deleteVideo(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);

        // 관리자 로깅 추가: 동영상 삭제 시도
        if (success) {
            AdminLogger.logRequest("o", "동영상 삭제 성공", "/api/video/" + id, "DELETE", "admin", "Video ID: " + id, request);
        } else {
            AdminLogger.logRequest("e", "동영상 삭제 실패", "/api/video/" + id, "DELETE", "admin", "Video ID: " + id, request);
        }

        return ResponseEntity.ok(response);
    }
}
