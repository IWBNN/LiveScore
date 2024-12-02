package ac.su.suport.livescore.domain;

import ac.su.suport.livescore.constant.LiveVideoStreamStatus;
import ac.su.suport.livescore.domain.ChatRoom;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "live_video_stream")
@Getter @Setter
public class LiveVideoStream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stream_id")
    private Long streamId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LiveVideoStreamStatus status;

    @OneToOne(mappedBy = "liveVideoStream")
    private ChatRoom liveChats;
}