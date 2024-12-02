package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.constant.SportEnum;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.SportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import java.util.List;

@RestController
@RequestMapping("/api/sports")
public class SportController {

    private final SportService sportService;

    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping
    public List<SportEnum> getAllSports(HttpServletRequest request) {
        List<SportEnum> sports = sportService.getAllSports();

        // 사용자 로깅 추가: 모든 스포츠 목록 조회
        UserLogger.logRequest("i", "모든 스포츠 목록 조회", "/api/sports", "GET", "user", "Retrieved all sports", request);

        return sports;
    }
}
