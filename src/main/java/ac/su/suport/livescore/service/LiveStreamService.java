package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.LiveVideoStreamStatus;
import ac.su.suport.livescore.domain.LiveVideoStream;
import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.repository.LiveVideoStreamRepository;
import ac.su.suport.livescore.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveStreamService {
    private final LiveVideoStreamRepository liveVideoStreamRepository;
    private final MatchRepository matchRepository;
    private final FFmpegService ffmpegService;
    private final S3Service s3Service;

    public LiveVideoStream startStream(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        LiveVideoStream stream = liveVideoStreamRepository.findByMatchMatchId(matchId)
                .orElse(new LiveVideoStream());

        stream.setMatch(match);
        stream.setStatus(LiveVideoStreamStatus.ACTIVE);
        LiveVideoStream savedStream = liveVideoStreamRepository.save(stream);

        ffmpegService.startWebcamStreaming(matchId.toString());

        return savedStream;
    }

    public void stopStream(Long matchId) {
        LiveVideoStream stream = liveVideoStreamRepository.findByMatchMatchId(matchId)
                .orElseThrow(() -> new RuntimeException("Stream not found"));

        stream.setStatus(LiveVideoStreamStatus.ENDED);
        liveVideoStreamRepository.save(stream);

        ffmpegService.stopStreaming(matchId.toString());

        // Upload HLS files to S3
        try {
            String filePath = "/tmp/hls/" + matchId + "/playlist.m3u8";
            String s3Key = "streams/" + matchId + "/playlist.m3u8";
            s3Service.uploadFile(new FileInputStream(filePath), s3Key, "application/vnd.apple.mpegurl");

            File dir = new File("/tmp/hls/" + matchId);
            for (File file : dir.listFiles((d, name) -> name.endsWith(".ts"))) {
                String tsS3Key = "streams/" + matchId + "/" + file.getName();
                s3Service.uploadFile(new FileInputStream(file), tsS3Key, "video/MP2T");
            }

            log.info("Stream files uploaded to S3 for match ID: {}", matchId);
        } catch (IOException e) {
            log.error("Error uploading stream files to S3", e);
        }

        // Upload WebM file to S3
        try {
            String webmFilePath = "/tmp/hls/" + matchId + "/" + matchId + ".webm";
            String s3Key = matchId + "/" + matchId + ".webm";
            s3Service.uploadFile(new FileInputStream(webmFilePath), s3Key, "video/webm");

            log.info("WebM file uploaded to S3 for match ID: {}", matchId);
        } catch (IOException e) {
            log.error("Error uploading WebM file to S3", e);
        }
    }

    public Optional<LiveVideoStream> getStreamByMatchId(Long matchId) {
        log.debug("Searching for stream with matchId: {}", matchId);
        Optional<LiveVideoStream> stream = liveVideoStreamRepository.findByMatchMatchId(matchId);
        if (stream.isEmpty()) {
            log.warn("Stream not found for matchId: {}", matchId);
        }
        return stream;
    }
}