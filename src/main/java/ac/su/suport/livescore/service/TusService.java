package ac.su.suport.livescore.service;

import ac.su.suport.livescore.Extractor.FFmpegExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TusService {

    private final TusFileUploadService tusFileUploadService;
    private final FFmpegExtractor fFmpegExtractor;
    private final VideoService videoService;
    private final S3Service s3Service;

    @Value("${tus.save.path}")
    private String savedPath;

    private final ConcurrentHashMap<String, UploadStatus> uploadStatuses = new ConcurrentHashMap<>();

    public String tusUpload(HttpServletRequest request, HttpServletResponse response) {
        String uploadUri = request.getRequestURI();
        UploadStatus status = uploadStatuses.computeIfAbsent(uploadUri, k -> new UploadStatus());

        if (status.isPaused()) {
            return "paused";
        }

        try {
            tusFileUploadService.process(request, response);
            UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(uploadUri);

            if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
                return completeUpload(uploadInfo, uploadUri, response);
            }

            status.setProgress(uploadInfo != null ? uploadInfo.getOffset() : 0);
            return "in_progress";
        } catch (IOException | TusException e) {
            log.error("Exception occurred. message={}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String completeUpload(UploadInfo uploadInfo, String uploadUri, HttpServletResponse response) throws IOException, TusException {
        String matchIdStr = uploadInfo.getMetadata().get("matchId");
        if (matchIdStr == null || matchIdStr.isEmpty()) {
            throw new IllegalArgumentException("Match ID is required and cannot be null or empty");
        }
        Long matchId = Long.valueOf(matchIdStr);

        log.info("Completing upload for matchId: {}", matchId);

        Path uploadedFilePath = saveUploadedFile(tusFileUploadService.getUploadedBytes(uploadUri), uploadInfo.getFileName(), matchId);
        String savedFileName = uploadedFilePath.getFileName().toString();

        LocalDate today = LocalDate.now();
        videoService.addVideo(today.toString(), matchId, savedFileName);

        tusFileUploadService.deleteUpload(uploadUri);

        uploadToS3(uploadedFilePath, matchId, savedFileName);

        uploadStatuses.remove(uploadUri);
        response.sendRedirect("/api/matches");
        return "success";
    }


    private Path saveUploadedFile(InputStream is, String filename, Long matchId) throws IOException {
        LocalDate today = LocalDate.now();
        Path uploadedPath = Paths.get(savedPath, today.toString(), matchId.toString());
        String extension = filename.substring(filename.lastIndexOf('.'));

        log.info("Creating directories and saving file at: {}", uploadedPath);

        Files.createDirectories(uploadedPath);
        String vodName = getUniqueVodName(matchId, today.toString(), extension, uploadedPath.toString());
        Path filePath = uploadedPath.resolve(vodName);

        Files.copy(is, filePath);

        log.info("File saved successfully at: {}", filePath.toAbsolutePath());

        fFmpegExtractor.getThumbnail(filePath.toString());
        fFmpegExtractor.getDuration(filePath.toString());

        return filePath;
    }

    private String getVodName(Long matchId, String date, String extension) {
        return matchId + "_" + date + extension;
    }

    private String getUniqueVodName(Long matchId, String date, String extension, String directory) throws IOException {
        String uniqueName = getVodName(matchId, date, extension);
        int counter = 1;

        while (Files.exists(Paths.get(directory, uniqueName))) {
            uniqueName = matchId + "_" + date + "(" + counter + ")" + extension;
            counter++;
        }

        return uniqueName;
    }
    private void uploadToS3(Path filePath, Long matchId, String fileName) throws IOException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            String s3Key = matchId + "/" + fileName;
            String mimeType = Files.probeContentType(filePath);
            s3Service.uploadFile(inputStream, s3Key, mimeType);
        }
        log.info("File successfully uploaded to S3: {}", fileName);
    }

    public void pauseUpload(String uploadUri) {
        uploadStatuses.computeIfAbsent(uploadUri, k -> new UploadStatus()).setPaused(true);
    }

    public void resumeUpload(String uploadUri) {
        uploadStatuses.computeIfPresent(uploadUri, (k, v) -> {
            v.setPaused(false);
            return v;
        });
    }

    public UploadStatus getUploadStatus(String uploadUri) {
        return uploadStatuses.get(uploadUri);
    }

    // Existing private methods (saveUploadedFile, getVodName, getUniqueVodName) remain unchanged

    private static class UploadStatus {
        private boolean paused;
        private long progress;

        public boolean isPaused() {
            return paused;
        }

        public void setPaused(boolean paused) {
            this.paused = paused;
        }

        public long getProgress() {
            return progress;
        }

        public void setProgress(long progress) {
            this.progress = progress;
        }
    }
}