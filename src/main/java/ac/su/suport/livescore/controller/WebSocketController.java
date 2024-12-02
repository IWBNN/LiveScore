package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.logger.AdminLogger;  // AdminLogger 추가
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.websocketmessage.ChatMessage;
import ac.su.suport.livescore.websocketmessage.StreamMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpServletRequest;  // HttpServletRequest 추가

@Controller
public class WebSocketController {

    @MessageMapping("/offer")
    @SendTo("/topic/offer")
    public Message sendOffer(Message message, HttpServletRequest request) {
        // 사용자 로깅 추가: offer 메시지 전송
        UserLogger.logRequest("i", "offer 메시지 전송", "/topic/offer", "MESSAGE", "user", "Offer message sent", request);
        
        return message; // 스트리밍 offer 메시지 전송
    }

    @MessageMapping("/answer")
    @SendTo("/topic/answer")
    public Message sendAnswer(Message message, HttpServletRequest request) {
        // 사용자 로깅 추가: answer 메시지 전송
        UserLogger.logRequest("i", "answer 메시지 전송", "/topic/answer", "MESSAGE", "user", "Answer message sent", request);
        
        return message; // 스트리밍 answer 메시지 전송
    }

    @MessageMapping("/candidate")
    @SendTo("/topic/candidate")
    public Message sendCandidate(Message message, HttpServletRequest request) {
        // 사용자 로깅 추가: candidate 메시지 전송
        UserLogger.logRequest("i", "candidate 메시지 전송", "/topic/candidate", "MESSAGE", "user", "Candidate message sent", request);
        
        return message; // 스트리밍 candidate 메시지 전송
    }

    @MessageMapping("/stream/{matchId}")
    @SendTo("/topic/stream/{matchId}")
    public StreamMessage handleStreamMessage(@DestinationVariable String matchId, StreamMessage message, HttpServletRequest request) {
        // 사용자 로깅 추가: 스트림 메시지 처리
        UserLogger.logRequest("i", "스트림 메시지 처리", "/topic/stream/" + matchId, "MESSAGE", "user", "Stream message sent for match ID: " + matchId, request);
        
        return message; // 특정 매치의 스트리밍 메시지 전송
    }

    @MessageMapping("/chat/{matchId}")
    @SendTo("/topic/chat/{matchId}")
    public ChatMessage handleChatMessage(@DestinationVariable String matchId, ChatMessage message, HttpServletRequest request) {
        // 사용자 로깅 추가: 채팅 메시지 처리
        UserLogger.logRequest("i", "채팅 메시지 처리", "/topic/chat/" + matchId, "MESSAGE", "user", "Chat message sent for match ID: " + matchId, request);
        
        return message; // 특정 매치의 채팅 메시지 전송
    }
}
