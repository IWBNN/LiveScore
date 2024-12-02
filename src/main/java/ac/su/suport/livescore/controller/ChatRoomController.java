package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.constant.UserRole;
import ac.su.suport.livescore.domain.ChatRoom;
import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.repository.ChatRoomRepository;
import ac.su.suport.livescore.service.BanService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    private static final Logger logger = LoggerFactory.getLogger(ChatRoomController.class);
    private final ChatRoomRepository chatRoomRepository;
    private final BanService banService;

    // 현재 인증된 사용자의 정보를 반환하는 엔드포인트
    @GetMapping("/user")
    public ResponseEntity<String> getUserInfo(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            logger.warn("Unauthenticated access attempt.");
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        // 사용자 로깅 추가: 사용자 정보 조회
        UserLogger.logRequest("i", "사용자 정보 조회", "/api/chat/user", "GET", "user", "Authenticated user: " + auth.getName(), request);

        logger.info("Authenticated user: {}", auth.getName());
        return new ResponseEntity<>(auth.getName(), HttpStatus.OK);
    }

    // 모든 채팅방 목록을 반환하는 엔드포인트
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getRooms(HttpServletRequest request) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom();
        chatRooms.forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId())));

        // 사용자 로깅 추가: 모든 채팅방 목록 조회
        UserLogger.logRequest("i", "모든 채팅방 목록 조회", "/api/chat/rooms", "GET", "user", "Fetched chat rooms", request);

        logger.info("Fetched {} chat rooms.", chatRooms.size());
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    // 새로운 채팅방을 생성하는 엔드포인트
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createRoom(@RequestParam String matchId, @RequestParam String name, HttpServletRequest request) {
        ChatRoom newRoom = chatRoomRepository.createChatRoom(matchId, name);

        // 관리자 로깅 추가: 새로운 채팅방 생성
        AdminLogger.logRequest("i", "새로운 채팅방 생성", "/api/chat/room", "POST", "admin", "Created chat room: " + name + " with matchId: " + matchId, request);

        logger.info("Created new chat room: {} with matchId: {}", name, matchId);
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED);
    }

    // 특정 채팅방의 정보를 조회하는 엔드포인트
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoom> getRoomInfo(@PathVariable String roomId, HttpServletRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);
        if (chatRoom == null) {
            logger.warn("Chat room with ID {} not found.", roomId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 사용자 로깅 추가: 특정 채팅방 정보 조회
        UserLogger.logRequest("i", "특정 채팅방 정보 조회", "/api/chat/room/" + roomId, "GET", "user", "Fetched chat room info for roomId: " + roomId, request);

        logger.info("Fetched chat room info for roomId: {}", roomId);
        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    // 특정 채팅방에 대한 상세 정보를 반환하는 엔드포인트
    @GetMapping("/room/enter/{roomId}")
    public ResponseEntity<ChatRoom> getRoomDetail(@PathVariable String roomId, HttpServletRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findRoomById(roomId);
        if (chatRoom == null) {
            logger.warn("Chat room with ID {} not found.", roomId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 사용자 로깅 추가: 특정 채팅방 상세 정보 조회
        UserLogger.logRequest("i", "특정 채팅방 상세 정보 조회", "/api/chat/room/enter/" + roomId, "GET", "user", "Fetched chat room details for roomId: " + roomId, request);

        logger.info("Fetched chat room details for roomId: {}", roomId);
        return new ResponseEntity<>(chatRoom, HttpStatus.OK);
    }

    // ADMIN이 특정 사용자를 채팅방에서 강퇴하는 엔드포인트
    @PostMapping("/room/{roomId}/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable String roomId, @PathVariable String userId, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            logger.warn("Unauthorized ban attempt by an unauthenticated user.");
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }

        UserRole role = UserRole.valueOf(auth.getAuthorities().iterator().next().getAuthority());

        try {
            banService.banUser(roomId, userId, role);

            // 관리자 로깅 추가: 사용자를 채팅방에서 강퇴
            AdminLogger.logRequest("i", "사용자 강퇴", "/api/chat/room/" + roomId + "/ban/" + userId, "POST", "admin", "User ID: " + userId + " banned from room: " + roomId + " by admin: " + auth.getName(), request);

            logger.info("User with ID {} banned from room {} by admin {}.", userId, roomId, auth.getName());
            return new ResponseEntity<>("User banned successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to ban user {} from room {}. Reason: {}", userId, roomId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    // 특정 사용자가 해당 채팅방에서 강퇴되었는지 확인하는 엔드포인트
    @GetMapping("/room/{roomId}/isBanned/{userId}")
    public ResponseEntity<Boolean> isUserBanned(@PathVariable String roomId, @PathVariable String userId, HttpServletRequest request) {
        boolean isBanned = banService.isUserBanned(roomId, userId);

        // 사용자 로깅 추가: 특정 사용자가 채팅방에서 강퇴되었는지 확인
        UserLogger.logRequest("i", "사용자 강퇴 여부 확인", "/api/chat/room/" + roomId + "/isBanned/" + userId, "GET", "user", "Checked if user ID: " + userId + " is banned from room: " + roomId, request);

        logger.info("Check if user {} is banned from room {}: {}", userId, roomId, isBanned);
        return new ResponseEntity<>(isBanned, HttpStatus.OK);
    }
}
