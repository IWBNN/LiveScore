package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.service.TusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/upload/tus")
public class TusController {

    private final TusService tusService;

    @ResponseBody
    @RequestMapping(value = {"", "/**"}, method = {RequestMethod.POST, RequestMethod.PATCH})
    public ResponseEntity<String> tusUpload(HttpServletRequest request, HttpServletResponse response) {
        String result = tusService.tusUpload(request, response);

        // 관리자 로깅 추가: TUS 업로드 처리
        if ("success".equals(result)) {
            AdminLogger.logRequest("i", "TUS 업로드 성공", "/api/upload/tus", request.getMethod(), "admin", "Upload successful", request);
            return ResponseEntity.ok("업로드 완료! /matches 페이지로 리디렉션합니다.");
        } else {
            AdminLogger.logRequest("e", "TUS 업로드 실패", "/api/upload/tus", request.getMethod(), "admin", "Upload failed: " + result, request);
            return ResponseEntity.ok(result);
        }
    }
}
