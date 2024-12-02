package ac.su.suport.livescore.config;

import ac.su.suport.livescore.domain.User;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                attributes.put("nickname", currentUser.getNickname());
//                attributes.put("userId", currentUser.getUserId());
                System.out.println("WebSocketHandshakeInterceptor: Nickname set to " + currentUser.getNickname());
            } else {
                System.out.println("WebSocketHandshakeInterceptor: No current user in session");
                attributes.put("nickname", "Anonymous");
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 필요한 경우 여기에 추가 로직을 구현할 수 있습니다.
    }
}