package ac.su.suport.livescore.service;

import ac.su.suport.livescore.domain.Comment;
import ac.su.suport.livescore.dto.CommentDTO;
import ac.su.suport.livescore.dto.DeleteCommentRequest;
import ac.su.suport.livescore.repository.CommentRepository;
import ac.su.suport.livescore.repository.UserRepository;
import ac.su.suport.livescore.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<CommentDTO> getCommentById(Long commentId) {
        return commentRepository.findById(commentId).map(this::toDTO);
    }


    public CommentDTO createComment(Long matchId, Long userId, CommentDTO commentDTO) {
        Comment comment = new Comment();
        comment.setUser(userRepository.findById(userId).orElseThrow());
        comment.setMatch(matchRepository.findById(matchId).orElseThrow());
        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(commentDTO.getCreatedAt());
        Comment savedComment = commentRepository.save(comment);
        return toDTO(savedComment);
    }

    public Optional<CommentDTO> updateComment(Long commentId, CommentDTO commentDTO) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setContent(commentDTO.getContent());
            comment.setCreatedAt(commentDTO.getCreatedAt());
            Comment updatedComment = commentRepository.save(comment);
            return toDTO(updatedComment);
        });
    }

//    public void deleteComment(Long commentId) {
//        commentRepository.deleteById(commentId);
//    }

    // 댓글 삭제 메소드
    public boolean deleteComment(DeleteCommentRequest request) {
        // 댓글 ID로 댓글 조회
        Optional<Comment> commentOpt = commentRepository.findById(request.getCommentId());

        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            // 댓글 작성자 ID와 요청한 유저 ID가 일치하는지 확인
            if (comment.getUser().getUserId().equals(request.getUserId())) {
                // 일치하면 댓글 삭제
                commentRepository.deleteById(request.getCommentId());
                return true;
            }
        }
        // 일치하지 않거나 댓글이 존재하지 않으면 false 반환
        return false;
    }



    private CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentId(comment.getCommentId());
        commentDTO.setUserId(comment.getUser().getUserId());
        commentDTO.setMatchId(comment.getMatch().getMatchId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        return commentDTO;
    }

    public List<CommentDTO> getCommentsByMatchId(Long matchId) {
        return commentRepository.findByMatchMatchId(matchId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
