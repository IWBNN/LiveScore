package ac.su.suport.livescore.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    public enum MessageType {
        JOIN, TALK, QUIT // 메시지 타입 정의
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 메시지가 속한 채팅방 ID
    private String sender; // 메시지 보낸 사람 (username)
    private String nickname; // 보낸 사람의 닉네임
    private String message; // 메시지 내용
    private int userCount; // 현재 접속자 수

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String nickname, String message, int userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.nickname = nickname;
        this.message = message;
        this.userCount = userCount;
    }
}

