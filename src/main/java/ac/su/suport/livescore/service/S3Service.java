package ac.su.suport.livescore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String s3bucket;

    public InputStream getFile(Long matchId, String filename) {
        String key = matchId + "/" + filename;
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    public void uploadFile(InputStream inputStream, String key, String contentType) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
    }

    public void deleteFile(Long matchId, String filename) {
        String key = matchId + "/" + filename;
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}