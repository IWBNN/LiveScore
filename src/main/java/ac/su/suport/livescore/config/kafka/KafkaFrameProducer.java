package ac.su.suport.livescore.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFrameProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private static final String FRAME_TOPIC = "video-frames";

    public void sendFrame(String streamId, byte[] frameData) {
        CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(FRAME_TOPIC, streamId, frameData);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Sent frame for stream ID: {}. Offset: {}", streamId, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send frame for stream ID: {}", streamId, ex);
            }
        });
    }
}