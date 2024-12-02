package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.domain.User;
import ac.su.suport.livescore.dto.UserDTO;
import ac.su.suport.livescore.dto.LoginRequest;
import ac.su.suport.livescore.exception.CustomException;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final HttpSession session;

    public UserController(UserService userService, HttpSession session) {
        this.userService = userService;
        this.session = session;
    }

    // 사용자 등록
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        User registeredUser = userService.registerUser(userDTO);

        // 사용자 로깅 추가: 사용자 등록
        UserLogger.logRequest("i", "사용자 등록", "/api/users/register", "POST", "user", "Registered User: " + registeredUser.getUsername(), request);

        return ResponseEntity.ok("회원가입이 정상적으로 완료되었습니다, " + registeredUser.getUsername() + " 님.");
    }

    // 이메일 중복 확인
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email, HttpServletRequest request) {
        boolean isTaken = userService.isEmailTaken(email);

        // 사용자 로깅 추가: 이메일 중복 확인
        UserLogger.logRequest("i", "이메일 중복 확인", "/api/users/checkEmail", "GET", "user", "Email Checked: " + email + ", Taken: " + isTaken, request);

        return ResponseEntity.ok(isTaken);
    }

    // 닉네임 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname, HttpServletRequest request) {
        boolean isTaken = userService.isNicknameTaken(nickname);

        // 사용자 로깅 추가: 닉네임 중복 확인
        UserLogger.logRequest("i", "닉네임 중복 확인", "/api/users/checkNickname", "GET", "user", "Nickname Checked: " + nickname + ", Taken: " + isTaken, request);

        return ResponseEntity.ok(isTaken);
    }

    // 인증 코드 전송
    @PostMapping("/sendVerificationCode")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email, HttpServletRequest request) {
        try {
            userService.sendVerificationCode(email);

            // 사용자 로깅 추가: 인증 코드 전송
            UserLogger.logRequest("i", "인증 코드 전송", "/api/users/sendVerificationCode", "POST", "user", "Verification Code Sent to: " + email, request);

            return ResponseEntity.ok("인증 코드가 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 인증 코드 확인
    @PostMapping("/verifyCode")
    public ResponseEntity<Boolean> verifyCode(@RequestParam String email, @RequestParam String code, HttpServletRequest request) {
        boolean isValid = userService.verifyCode(email, code);

        // 사용자 로깅 추가: 인증 코드 확인
        UserLogger.logRequest("i", "인증 코드 확인", "/api/users/verifyCode", "POST", "user", "Verification Code Checked for Email: " + email + ", Code: " + code + ", Valid: " + isValid, request);

        return ResponseEntity.ok(isValid);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (user == null) {
            // 로그인 실패 시 로그 기록
            UserLogger.logRequest("e", "로그인 실패", "/api/users/login", "POST", "user", "Failed login attempt with email: " + loginRequest.getEmail(), request);

            // 로그인 실패 시 응답
            return ResponseEntity.badRequest().body(Map.of("error", "유효하지 않은 이메일 혹은 비밀번호입니다."));
        }

        // 로그인 성공 시 세션 설정
        session.setAttribute("currentUser", user);

        // 응답 객체에 userId, role, nickname 추가
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그인 성공");
        response.put("userId", String.valueOf(user.getUserId())); // userId 추가
        response.put("role", user.getRole().name());
        response.put("nickname", user.getNickname());
        response.put("email", user.getEmail());

        // 로그인 성공 시 로그 기록
        UserLogger.logRequest("i", "로그인 성공", "/api/users/login", "POST", "user", "User: " + user.getUsername() + " logged in", request);

        // 성공 응답 반환
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            // 사용자 로깅 추가: 로그아웃
            UserLogger.logRequest("i", "로그아웃", "/api/users/logout", "POST", "user", "User: " + currentUser.getUsername() + " logged out", request);
        }
        session.invalidate();
        return ResponseEntity.ok("로그아웃 성공");
    }

    // 사용자 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("사용자가 인증되지 않았습니다.");
        }

        userService.deleteUser(currentUser);
        session.invalidate();

        // 관리자 로깅 추가: 사용자 삭제
        AdminLogger.logRequest("o", "사용자 삭제", "/api/users/delete", "DELETE", "admin", "User: " + currentUser.getUsername(), request);

        return ResponseEntity.ok("회원탈퇴 성공");
    }

    // 현재 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", currentUser.getUsername());
        userInfo.put("email", currentUser.getEmail());
        userInfo.put("nickname", currentUser.getNickname());
        userInfo.put("role", currentUser.getRole().name());

        // 사용자 로깅 추가: 현재 사용자 정보 조회
        UserLogger.logRequest("i", "현재 사용자 정보 조회", "/api/users/me", "GET", "user", "User: " + currentUser.getUsername() + " accessed their information", request);

        return ResponseEntity.ok(userInfo);
    }

    // 사용자 정보 업데이트
    @PostMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("사용자가 인증되지 않았습니다.");
        }

        try {
            userService.updateUser(userDTO, currentUser);
            session.setAttribute("currentUser", currentUser); // 업데이트 후 세션 갱신

            // 관리자 로깅 추가: 사용자 정보 업데이트
            AdminLogger.logRequest("i", "사용자 정보 업데이트", "/api/users/update", "POST", "admin", "User: " + currentUser.getUsername(), request);

            return ResponseEntity.ok("사용자 정보가 업데이트되었습니다.");
        } catch (CustomException e) {
            // 사용자 정보 업데이트 실패 로깅
            AdminLogger.logRequest("e", "사용자 정보 업데이트 실패", "/api/users/update", "POST", "admin", "User: " + currentUser.getUsername() + ", Error: " + e.getMessage(), request);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 비밀번호 찾기
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email, HttpServletRequest request) {
        try {
            userService.resetPassword(email);

            // 사용자 로깅 추가: 비밀번호 찾기
            UserLogger.logRequest("i", "비밀번호 찾기", "/api/users/forgotPassword", "POST", "user", "Password reset requested for email: " + email, request);

            return ResponseEntity.ok("새로운 비밀번호가 이메일로 전송되었습니다.");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
