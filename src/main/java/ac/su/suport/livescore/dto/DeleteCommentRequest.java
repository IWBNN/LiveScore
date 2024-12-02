package ac.su.suport.livescore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCommentRequest {
    // 삭제 요청 시 필요한 유저 ID와 댓글 ID를 포함하는 DTO
    private Long userId;
    private Long commentId;
}
