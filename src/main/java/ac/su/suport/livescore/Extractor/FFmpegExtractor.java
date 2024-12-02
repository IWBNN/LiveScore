package ac.su.suport.livescore.Extractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class FFmpegExtractor {

    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;

    private static final String THUMBNAIL_EXTENSION = ".png";
    private static final String DEFAULT_IMAGE_PATH = "src/main/resources/static/images/default-thumbnail.png";


    public void getThumbnail(String sourcePath) {
        // 썸네일 저장할 경로
        final String outputPath = sourcePath.split("\\.")[0] + THUMBNAIL_EXTENSION;

        try {
            // ffmpeg cli 명령어 생성
            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(sourcePath)
                    .overrideOutputFiles(true)
                    .addOutput(outputPath)
                    .setFormat("image2")
                    .setFrames(1)
                    .setVideoFrameRate(1)
                    .done();

            // 명령어 실행
            ffmpeg.run(builder);
        } catch (Exception e) {
            // 썸네일 추출 실패시 기본 이미지 썸네일로 사용
            File thumbnail = new File(outputPath);
            File defaultImage = new File(DEFAULT_IMAGE_PATH);

            try {
                FileUtils.copyFile(defaultImage, thumbnail);
            } catch (Exception ex) {
                log.error("Thumbnail Extract Failed => {}", sourcePath, e);
            }
        }
    }

    public void getDuration(String sourcePath) throws IOException {
        // 영상 경로
        Path videoPath = Paths.get(sourcePath);

        // 영상 메타데이터 조회
        FFmpegProbeResult probeResult = ffprobe.probe(videoPath.toString());

        // 영상 길이 추출
        FFmpegStream videoStream = probeResult.getStreams().get(0);
        double durationInSeconds = videoStream.duration;

        System.out.println("Video length: " + durationInSeconds + " seconds");
    }

}