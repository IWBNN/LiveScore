package ac.su.suport.livescore.config.kafka;

import ac.su.suport.livescore.service.FFmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFrameConsumer {

    private final FFmpegService ffmpegService;

    @KafkaListener(topics = "video-frames", groupId = "frame-processing-group")
    public void consume(ConsumerRecord<String, byte[]> record) {
        String streamId = record.key();
        byte[] frameData = record.value();
        log.debug("Received frame for stream ID: {}. Frame size: {} bytes", streamId, frameData.length);
        ffmpegService.processWebcamData(streamId, frameData);
    }
}