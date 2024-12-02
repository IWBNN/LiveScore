package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // 모든 학과 목록 조회
    @GetMapping
    public List<DepartmentEnum> getAllDepartments(HttpServletRequest request) {
        List<DepartmentEnum> departments = departmentService.getAllDepartments();

        // 사용자 로깅 추가: 모든 학과 목록 조회
        UserLogger.logRequest("i", "모든 학과 목록 조회", "/api/departments", "GET", "user", "All departments retrieved", request);

        return departments;
    }
}
