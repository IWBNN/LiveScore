package ac.su.suport.livescore.service;

import ac.su.suport.livescore.dto.ChatMessage;
import ac.su.suport.livescore.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1); // 경로에서 roomId 추출
        } else {
            return "";
        }
    }

    public void sendChatMessage(ChatMessage chatMessage) {
        // 메시지 타입에 따라 메시지 내용 설정
//        switch (chatMessage.getType()) {
//            case JOIN:
//                chatMessage.setMessage(chatMessage.getNickname() + "님이 방에 입장했습니다.");
//                chatMessage.setSender("[알림]");
//                break;
//            case TALK:
//                // TALK 타입일 때는 아무 것도 변경하지 않는다.
//                break;
//            case QUIT:
//                chatMessage.setMessage(chatMessage.getNickname() + "님이 방에서 나갔습니다.");
//                chatMessage.setSender("[알림]");
//                break;
        // Redis로 메시지 발행
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);

    }
}
