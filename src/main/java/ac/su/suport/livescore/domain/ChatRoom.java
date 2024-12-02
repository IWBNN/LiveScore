package ac.su.suport.livescore.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "chat_room")
@Getter @Setter
@NoArgsConstructor
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id
    @Column(name = "room_id")
    private String roomId; // 채팅방 ID

    @Column(name = "name")
    private String name; // 채팅방 이름

    @Column(name = "user_count")
    private Integer userCount; // 현재 채팅방의 사용자 수

    @OneToOne
    @JoinColumn(name = "stream_id")
    private LiveVideoStream liveVideoStream; // 해당 채팅방과 연동된 라이브 비디오 스트림

    // 채팅방을 생성할 때 사용하는 생성자
    public ChatRoom(String roomId, String name) {
        this.roomId = roomId;
        this.name = name;
        this.userCount = 0; // 채팅방 생성 시 사용자 수는 0으로 초기화
    }

    // 채팅방을 생성하는 정적 메소드
    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString(); // 랜덤 UUID로 방 ID 생성
        chatRoom.name = name;
        return chatRoom;
    }
}

