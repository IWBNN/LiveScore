package ac.su.suport.livescore.webrtc;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Message {
    private String type;
    private Object offer;
    private Object answer;
    private Object candidate;
    private String sender;
}
