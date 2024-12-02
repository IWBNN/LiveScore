package ac.su.suport.livescore.config;

import me.desair.tus.server.TusFileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TusConfig {

    @Value("${tus.data.path}")
    private String tusDataPath;

    @Value("${tus.data.expiration}")
    private Long tusDataExpiration;

    @Bean
    public TusFileUploadService tus() {
        return new TusFileUploadService()
                .withStoragePath(tusDataPath)
                .withDownloadFeature()
                .withUploadExpirationPeriod(tusDataExpiration)
                .withThreadLocalCache(true)
                .withUploadUri("/api/upload/tus"); // 수정된 업로드 엔드포인트 설정
    }
}

