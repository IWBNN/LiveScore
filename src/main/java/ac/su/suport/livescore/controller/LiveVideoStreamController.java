package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.config.kafka.KafkaFrameProducer;
import ac.su.suport.livescore.domain.LiveVideoStream;
import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.LiveVideoStreamService;
import ac.su.suport.livescore.service.FFmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://kim11.shop, https://suportscore.site"})
@RequestMapping("/api/live")
public class LiveVideoStreamController {
    private final LiveVideoStreamService liveVideoStreamService;
    private final FFmpegService ffmpegService;
    private final KafkaFrameProducer kafkaFrameProducer;

    @GetMapping("/match/{matchId}")
    public Mono<ResponseEntity<LiveVideoStream>> getLiveStream(@PathVariable Long matchId, HttpServletRequest request) {
        return liveVideoStreamService.getStreamByMatchId(matchId)
                .map(stream -> {
                    // 사용자 로깅 추가: 특정 경기의 라이브 스트림 조회
                    UserLogger.logRequest("i", "특정 경기의 라이브 스트림 조회", "/api/live/match/" + matchId, "GET", "user", "Live stream retrieved for match ID: " + matchId, request);
                    return ResponseEntity.ok(stream);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public Mono<ResponseEntity<List<LiveVideoStream>>> getAllActiveStreams(HttpServletRequest request) {
        return liveVideoStreamService.getAllActiveStreams()
                .map(streams -> {
                    // 사용자 로깅 추가: 모든 활성 라이브 스트림 조회
                    UserLogger.logRequest("i", "모든 활성 라이브 스트림 조회", "/api/live/active", "GET", "user", "Active live streams retrieved", request);
                    return ResponseEntity.ok(streams);
                });
    }

    @PostMapping("/webcam/start/{matchId}")
    public Mono<ResponseEntity<String>> startWebcamStream(@PathVariable Long matchId, HttpServletRequest request) {
        log.info("Received request to start webcam stream for match ID: {}", matchId);
        return liveVideoStreamService.startStream(matchId)
                .doOnSuccess(stream -> {
                    ffmpegService.startWebcamStreaming(matchId.toString());
                    log.info("Webcam stream started successfully for match ID: {}", matchId);

                    // 관리자 로깅 추가: 웹캠 스트림 시작
                    AdminLogger.logRequest("i", "웹캠 스트림 시작", "/api/live/webcam/start/" + matchId, "POST", "admin", "Webcam stream started for match ID: " + matchId, request);
                })
                .thenReturn(ResponseEntity.ok("Webcam stream started for match " + matchId))
                .onErrorResume(e -> {
                    log.error("Error starting webcam stream for match ID: {}", matchId, e);
                    // 관리자 로깅 추가: 웹캠 스트림 시작 실패
                    AdminLogger.logRequest("e", "웹캠 스트림 시작 실패", "/api/live/webcam/start/" + matchId, "POST", "admin", "Failed to start webcam stream for match ID: " + matchId + ", Error: " + e.getMessage(), request);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to start webcam stream: " + e.getMessage()));
                });
    }

    @PostMapping("/webcam/stop/{matchId}")
    public Mono<ResponseEntity<String>> stopWebcamStream(@PathVariable Long matchId, HttpServletRequest request) {
        log.info("Received request to stop webcam stream for match ID: {}", matchId);
        return liveVideoStreamService.stopStream(matchId)
                .doOnSuccess(unused -> {
                    try {
                        ffmpegService.stopStreaming(matchId.toString());
                        log.info("FFmpeg process stopped successfully for match ID: {}", matchId);
                        // 관리자 로깅 추가: 웹캠 스트림 중지
                        AdminLogger.logRequest("i", "웹캠 스트림 중지", "/api/live/webcam/stop/" + matchId, "POST", "admin", "Webcam stream stopped for match ID: " + matchId, request);
                    } catch (Exception e) {
                        log.error("Error stopping FFmpeg process for match ID: {}", matchId, e);
                        // 관리자 로깅 추가: FFmpeg 프로세스 중지 실패
                        AdminLogger.logRequest("e", "FFmpeg 프로세스 중지 실패", "/api/live/webcam/stop/" + matchId, "POST", "admin", "Failed to stop FFmpeg process for match ID: " + matchId + ", Error: " + e.getMessage(), request);
                        throw new RuntimeException("Failed to stop FFmpeg process", e);
                    }
                })
                .thenReturn(ResponseEntity.ok("Webcam stream stopped for match " + matchId))
                .onErrorResume(e -> {
                    log.error("Error stopping webcam stream for match ID: {}", matchId, e);
                    // 관리자 로깅 추가: 웹캠 스트림 중지 실패
                    AdminLogger.logRequest("e", "웹캠 스트림 중지 실패", "/api/live/webcam/stop/" + matchId, "POST", "admin", "Failed to stop webcam stream for match ID: " + matchId + ", Error: " + e.getMessage(), request);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to stop webcam stream: " + e.getMessage()));
                });
    }

    @PostMapping("/webcam/frame/{matchId}")
    public ResponseEntity<String> processWebcamFrame(@PathVariable Long matchId, @RequestParam("video") MultipartFile videoChunk, HttpServletRequest request) {
        try {
            byte[] frameData = videoChunk.getBytes();
            log.debug("Received frame data for match ID: {}. Size: {} bytes", matchId, frameData.length);
            // kafka 없이 스트리밍
            // liveVideoStreamService.processWebcamData(matchId.toString(), frameData);
            // kafka 를 이용하여 스트리밍
            kafkaFrameProducer.sendFrame(matchId.toString(), frameData);

            // 사용자 로깅 추가: 웹캠 프레임 처리
            UserLogger.logRequest("i", "웹캠 프레임 처리", "/api/live/webcam/frame/" + matchId, "POST", "user", "Frame data sent to Kafka for match ID: " + matchId, request);

            return ResponseEntity.ok("Frame data sent to Kafka for match " + matchId);
        } catch (IOException e) {
            log.error("Error processing webcam frame for match ID: {}", matchId, e);
            // 사용자 로깅 추가: 웹캠 프레임 처리 실패
            UserLogger.logRequest("e", "웹캠 프레임 처리 실패", "/api/live/webcam/frame/" + matchId, "POST", "user", "Failed to process webcam frame for match ID: " + matchId + ", Error: " + e.getMessage(), request);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process webcam frame: " + e.getMessage());
        }
    }
}
