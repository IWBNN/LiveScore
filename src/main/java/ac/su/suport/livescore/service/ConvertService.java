package ac.su.suport.livescore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConvertService {

    private final FFmpeg fFmpeg;
    private final FFprobe fFprobe;

    @Value("${tus.save.path}")
    private String savedPath;

    @Value("${tus.output.path.hls}")
    private String hlsOutputPath;

    public void convertToHls(String date, Long matchId ,String filename) {
        log.info("Starting HLS conversion for file: {}", filename);
        Path inputFilePath = Paths.get(savedPath, date, String.valueOf(matchId), filename);
        Path outputFolderPath = Paths.get(hlsOutputPath, filename.split("\\.")[0]);

        try {
            FFmpegProbeResult probeResult = fFprobe.probe(inputFilePath.toString());

            FFmpegBuilder builder = new FFmpegBuilder()
                    .setInput(inputFilePath.toString())
                    .addOutput(outputFolderPath.resolve("%v/playlist.m3u8").toString())
                    .setFormat("hls")
                    .addExtraArgs("-hls_time", "10")
                    .addExtraArgs("-hls_list_size", "0")
                    .addExtraArgs("-hls_segment_filename", outputFolderPath.resolve("%v/output_%03d.ts").toString())
                    .addExtraArgs("-master_pl_name", "master.m3u8")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-map", "0:v")
                    .addExtraArgs("-var_stream_map", "v:0,name:1080 v:1,name:720 v:2,name:480")
                    .addExtraArgs("-b:v:0", "5000k")
                    .addExtraArgs("-maxrate:v:0", "5000k")
                    .addExtraArgs("-bufsize:v:0", "10000k")
                    .addExtraArgs("-s:v:0", "1920x1080")
                    .addExtraArgs("-crf:v:0", "15")
                    .addExtraArgs("-b:a:0", "128k")
                    .addExtraArgs("-b:v:1", "2500k")
                    .addExtraArgs("-maxrate:v:1", "2500k")
                    .addExtraArgs("-bufsize:v:1", "5000k")
                    .addExtraArgs("-s:v:1", "1280x720")
                    .addExtraArgs("-crf:v:1", "22")
                    .addExtraArgs("-b:a:1", "96k")
                    .addExtraArgs("-b:v:2", "1000k")
                    .addExtraArgs("-maxrate:v:2", "1000k")
                    .addExtraArgs("-bufsize:v:2", "2000k")
                    .addExtraArgs("-s:v:2", "854x480")
                    .addExtraArgs("-crf:v:2", "28")
                    .addExtraArgs("-b:a:2", "64k")
                    .done();

            FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);
            FFmpegJob job = executor.createJob(builder, progress -> {
                log.info("progress: {}", progress);
                if (progress.status == Progress.Status.END) {
                    log.info("Conversion completed successfully");
                }
            });

            job.run();
        } catch (IOException e) {
            log.error("Error converting video to HLS: {}", e.getMessage());
        }
    }
}
