package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 주어진 매치 ID를 기반으로 댓글 목록을 찾는 메소드
     *
     * @param matchId 매치의 ID
     * @return 매치에 해당하는 댓글 목록
     */
    List<Comment> findByMatchMatchId(Long matchId);
}
