package ac.su.suport.livescore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class HlsService {

    @Value("${tus.output.path.hls}")
    private String hlsOutputPath;

    public File getHlsFile(String matchId, String filename) {
        return new File(hlsOutputPath + "/" + matchId + "/" + filename);
    }
}
