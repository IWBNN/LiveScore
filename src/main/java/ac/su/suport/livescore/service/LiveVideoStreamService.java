package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.LiveVideoStreamStatus;
import ac.su.suport.livescore.domain.LiveVideoStream;
import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.repository.LiveVideoStreamRepository;
import ac.su.suport.livescore.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveVideoStreamService {
    private final LiveVideoStreamRepository liveVideoStreamRepository;
    private final MatchRepository matchRepository;
    private final FFmpegService ffmpegService;

    public Mono<LiveVideoStream> getStreamByMatchId(Long matchId) {
        return Mono.defer(() -> Mono.justOrEmpty(liveVideoStreamRepository.findByMatchMatchId(matchId)));
    }

    public Mono<LiveVideoStream> startStream(Long matchId) {
        return Mono.fromCallable(() -> {
            Match match = matchRepository.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match not found with ID: " + matchId));

            LiveVideoStream stream = liveVideoStreamRepository.findByMatchMatchId(matchId)
                    .orElse(new LiveVideoStream());

            stream.setMatch(match);
            stream.setStatus(LiveVideoStreamStatus.ACTIVE);
            LiveVideoStream savedStream = liveVideoStreamRepository.save(stream);

            log.info("Stream record created for match ID: {}", matchId);

            return savedStream;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> stopStream(Long matchId) {
        return Mono.fromRunnable(() -> {
            LiveVideoStream stream = liveVideoStreamRepository.findByMatchMatchId(matchId)
                    .orElseThrow(() -> new RuntimeException("Stream not found"));

            stream.setStatus(LiveVideoStreamStatus.ENDED);
            liveVideoStreamRepository.save(stream);

            log.info("Stream record updated to ENDED for match ID: {}", matchId);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    public Mono<List<LiveVideoStream>> getAllActiveStreams() {
        return Mono.fromCallable(() -> liveVideoStreamRepository.findByStatus(String.valueOf(LiveVideoStreamStatus.ACTIVE)))
                .subscribeOn(Schedulers.boundedElastic());
    }

//    public void processWebcamData(String matchId, byte[] frameData) {
//        ffmpegService.processWebcamData(matchId, frameData);
//    }
}