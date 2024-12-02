package ac.su.suport.livescore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FFmpegService {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${rtmp.server.url}")
    private String rtmpServerUrl;

    private final ConcurrentHashMap<String, Process> runningProcesses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, OutputStream> processInputStreams = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void startWebcamStreaming(String streamId) {
        log.info("Starting webcam streaming for stream ID: {}", streamId);

        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.addAll(List.of(
                "-f", "webm",
                "-i", "pipe:0",
                "-vf", "scale=1280:720",  // 해상도를 720p로 증가
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-tune", "zerolatency",
                "-b:v", "2000k",  // 비디오 비트레이트를 2Mbps로 증가
                "-maxrate", "2.5M",  // 최대 비트레이트를 2.5Mbps로 증가
                "-bufsize", "5M",  // 버퍼 크기를 5M로 증가
                "-c:a", "aac",
                "-ar", "44100",
                "-b:a", "160k",  // 오디오 비트레이트를 160kbps로 증가
                "-g", "60",  // GOP 크기를 60으로 증가 (2초마다 키프레임)
                "-f", "flv",
                rtmpServerUrl + "/" + streamId
        ));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            runningProcesses.put(streamId, process);
            processInputStreams.put(streamId, process.getOutputStream());

            executorService.submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("FFmpeg output: {}", line);
                    }
                } catch (IOException e) {
                    log.error("Error reading FFmpeg output", e);
                }
            });

            log.info("FFmpeg process started successfully for stream ID: {}", streamId);
        } catch (IOException e) {
            log.error("Error starting FFmpeg process for stream ID: {}", streamId, e);
            throw new RuntimeException("Failed to start FFmpeg process", e);
        }
    }

    public void processWebcamData(String streamId, byte[] data) {
        log.debug("Processing frame data for stream ID: {}. Data size: {} bytes", streamId, data.length);
        OutputStream outputStream = processInputStreams.get(streamId);
        if (outputStream != null) {
            try {
                outputStream.write(data);
                outputStream.flush();
                log.debug("Wrote frame data to FFmpeg for stream ID: {}", streamId);
            } catch (IOException e) {
                log.error("Error writing webcam data to FFmpeg for stream ID: {}", streamId, e);
            }
        } else {
            log.warn("No FFmpeg process found for stream ID: {}", streamId);
        }
    }

    public void stopStreaming(String streamId) {
        Process process = runningProcesses.remove(streamId);
        OutputStream outputStream = processInputStreams.remove(streamId);

        if (process != null) {
            try {
                process.destroy();
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
                log.info("Stopped streaming for stream ID: {}", streamId);
            } catch (InterruptedException e) {
                log.error("Error waiting for process to terminate for stream ID: {}", streamId, e);
                Thread.currentThread().interrupt();
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("Error closing output stream for stream ID: {}", streamId, e);
            }
        }
    }
}