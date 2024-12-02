package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.domain.User;
import ac.su.suport.livescore.dto.CommentDTO;
import ac.su.suport.livescore.dto.DeleteCommentRequest;
import ac.su.suport.livescore.logger.AdminLogger;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 모든 댓글 조회
    @GetMapping
    public List<CommentDTO> getAllComments(HttpServletRequest request) {
        List<CommentDTO> comments = commentService.getAllComments();

        // 사용자 로깅 추가: 모든 댓글 조회
        UserLogger.logRequest("i", "모든 댓글 조회", "/api/comments", "GET", "user", "All comments retrieved", request);

        return comments;
    }

    // 댓글 생성
    @PostMapping("/{matchId}")
    public CommentDTO createComment(@PathVariable Long matchId, @RequestBody CommentDTO commentDTO, HttpSession session, HttpServletRequest request) {
        Long userId = ((User) session.getAttribute("currentUser")).getUserId();
        CommentDTO createdComment = commentService.createComment(matchId, userId, commentDTO);

        // 사용자 로깅 추가: 댓글 생성
        UserLogger.logRequest("o", "댓글 생성", "/api/comments/" + matchId, "POST", userId.toString(), "Comment created for matchId: " + matchId, request);

        return createdComment;
    }

    // 댓글 업데이트
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Optional<CommentDTO> updatedComment = commentService.updateComment(commentId, commentDTO);

        if (updatedComment.isPresent()) {
            // 사용자 로깅 추가: 댓글 수정 성공
            UserLogger.logRequest("o", "댓글 수정", "/api/comments/" + commentId, "PUT", "user", "Comment updated: " + commentId, request);
            return ResponseEntity.ok(updatedComment.get());
        } else {
            // 사용자 로깅 추가: 댓글 수정 실패
            UserLogger.logRequest("e", "댓글 수정 실패", "/api/comments/" + commentId, "PUT", "user", "Comment not found: " + commentId, request);
            return ResponseEntity.notFound().build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId, @RequestParam Long userId, HttpServletRequest Httprequest) {
        // DeleteCommentRequest 객체 생성
        DeleteCommentRequest request = new DeleteCommentRequest(userId, commentId);

        // CommentService를 통해 댓글 삭제 시도
        boolean isDeleted = commentService.deleteComment(request);

        if (isDeleted) {
            // 삭제 성공 시 HTTP 200 응답 반환
            AdminLogger.logRequest("o", "댓글 삭제 성공", "/api/comments/" + commentId, "DELETE", userId.toString(), "Comment ID: " + commentId, Httprequest);

            // 사용자 로깅 추가: 댓글 삭제 성공
            UserLogger.logRequest("o", "댓글 삭제 성공", "/api/comments/" + commentId, "DELETE", userId.toString(), "Comment ID: " + commentId, Httprequest);

            return ResponseEntity.ok("Comment deleted successfully.");
        } else {
            // 권한이 없거나 실패 시 HTTP 403 응답 반환
            AdminLogger.logRequest("e", "댓글 삭제 실패", "/api/comments/" + commentId, "DELETE", userId.toString(), "Comment ID: " + commentId, Httprequest);

            // 사용자 로깅 추가: 댓글 삭제 실패
            UserLogger.logRequest("e", "댓글 삭제 실패", "/api/comments/" + commentId, "DELETE", userId.toString(), "Comment ID: " + commentId, Httprequest);

            return ResponseEntity.status(403).body("You are not authorized to delete this comment.");
        }
    }

    // 특정 경기의 댓글 조회
    @GetMapping("/{matchId}")
    public List<CommentDTO> getCommentsByMatchId(@PathVariable Long matchId, HttpServletRequest request) {
        List<CommentDTO> comments = commentService.getCommentsByMatchId(matchId);

        // 사용자 로깅 추가: 특정 경기의 댓글 조회
        UserLogger.logRequest("i", "특정 경기의 댓글 조회", "/api/comments/" + matchId, "GET", "user", "Comments retrieved for matchId: " + matchId, request);

        return comments;
    }
}
