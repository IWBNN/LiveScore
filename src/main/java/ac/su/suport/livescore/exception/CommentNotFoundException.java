package ac.su.suport.livescore.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super("Comment not found with id: " + commentId);
    }
}
