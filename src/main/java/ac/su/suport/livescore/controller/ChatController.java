package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.dto.ChatMessage;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.repository.ChatRoomRepository;
import ac.su.suport.livescore.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    @MessageMapping("/chat/message/{matchId}")
    public void message(@DestinationVariable String matchId, ChatMessage message, SimpMessageHeaderAccessor headerAccessor, HttpServletRequest request) {
        String sessionId = headerAccessor.getSessionId();
        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "Anonymous";
        }
        
        // 사용자 로깅 추가: 메시지 수신
        UserLogger.logRequest("i", "메시지 수신", "/chat/message/" + matchId, "MESSAGE", "user", "Received message from user: " + nickname, request);

        logger.debug("Received message from user: {}", nickname);

        message.setSender(nickname);
        message.setNickname(nickname);
        message.setRoomId(matchId);

        // 채팅방 인원수 세팅
        int userCount = (int) chatRoomRepository.getUserCount(matchId);
        logger.debug("Current user count in room {}: {}", matchId, userCount);
        message.setUserCount(userCount);

        // 사용자 로깅 추가: 메시지 전송
        UserLogger.logRequest("i", "메시지 전송", "/chat/message/" + matchId, "MESSAGE", "user", "Message sent to room " + matchId + " by user: " + nickname, request);

        // Websocket 에 발행된 메시지를 redis 로 발행 (publish)
        chatService.sendChatMessage(message);
        logger.info("Message sent to room {}: {}", matchId, message);
    }
}
