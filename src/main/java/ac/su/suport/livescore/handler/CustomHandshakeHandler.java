package ac.su.suport.livescore.handler;//package ac.su.suport.livescore.handler;
//
//import ac.su.suport.livescore.domain.User;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import java.security.Principal;
//import java.util.Map;
//
//public class CustomHandshakeHandler extends DefaultHandshakeHandler {
//
//    @Override
//    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//        HttpSession session = getSession(request);
//        if (session != null) {
//            User currentUser = (User) session.getAttribute("currentUser");
//            if (currentUser != null) {
//                attributes.put("username", currentUser.getUsername());
//            }
//        }
//        return super.determineUser(request, wsHandler, attributes);
//    }
//
//    private HttpSession getSession(ServerHttpRequest request) {
//        if (request instanceof ServletServerHttpRequest) {
//            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//            return servletRequest.getSession(false);
//        }
//        return null;
//    }
//}
