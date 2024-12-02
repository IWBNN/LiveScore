//package ac.su.suport.livescore.config.kafka;
//
//import ac.su.suport.livescore.service.FFmpegService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.apache.kafka.streams.kstream.Consumed;
//import org.apache.kafka.streams.kstream.KStream;
//import org.springframework.stereotype.Component;
//import jakarta.annotation.PostConstruct;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class KafkaFrameProcessor {
//
//    private final FFmpegService ffmpegService;
//    private final StreamsBuilder streamsBuilder;
//
//    @PostConstruct
//    public void buildPipeline() {
//        KStream<String, byte[]> videoFrames = streamsBuilder.stream(
//                "video-frames",
//                Consumed.with(Serdes.String(), Serdes.ByteArray())
//        );
//
//        videoFrames
//                .peek((key, value) -> log.debug("Processing frame in Kafka Streams for stream ID: {}. Frame size: {} bytes", key, value.length))
//                .mapValues(frame -> {
//                    // 여기에 추가적인 프레임 처리 로직 구현
//                    // 예: 프레임 분석, 메타데이터 추출 등
//                    return frame;
//                })
//                .foreach(ffmpegService::processWebcamData);
//
//        // 필요한 경우, 처리된 프레임을 새로운 토픽으로 전송할 수 있습니다.
//        // .to("processed-video-frames", Produced.with(Serdes.String(), Serdes.ByteArray()));
//    }
//}