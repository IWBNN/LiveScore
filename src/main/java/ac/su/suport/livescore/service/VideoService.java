package ac.su.suport.livescore.service;

import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.domain.Video;
import ac.su.suport.livescore.repository.MatchRepository;
import ac.su.suport.livescore.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;
    private final MatchRepository matchRepository;
    private final S3Service s3Service;

    public void addVideo(String date, Long matchId, String filename) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isPresent()) {
            Video video = new Video();
            video.setDate(date);
            video.setMatch(matchOptional.get());
            video.setFilename(filename);
            videoRepository.save(video);
        } else {
            log.error("Match with id {} not found", matchId);
        }
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getVideosByMatchId(Long matchId) {
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isPresent()) {
            return videoRepository.findByMatch(matchOptional.get());
        } else {
            log.error("Match with id {} not found", matchId);
            return List.of();
        }
    }

    @Transactional
    public boolean deleteVideo(Long id) {
        Optional<Video> videoOptional = videoRepository.findById(id);
        if (videoOptional.isPresent()) {
            Video video = videoOptional.get();
            try {
                s3Service.deleteFile(video.getMatch().getMatchId(), video.getFilename());
                videoRepository.delete(video);
                log.info("Successfully deleted video with id {}", id);
                return true;
            } catch (Exception e) {
                log.error("Failed to delete video with id {}", id, e);
                return false;
            }
        }
        return false;
    }
}