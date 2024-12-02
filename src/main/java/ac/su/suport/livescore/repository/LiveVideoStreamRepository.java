package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.domain.LiveVideoStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface LiveVideoStreamRepository extends JpaRepository<LiveVideoStream, Long> {
    Optional<LiveVideoStream> findByMatchMatchId(Long matchId);

    List<LiveVideoStream> findByStatus(String status);

    List<LiveVideoStream> findAllByMatchMatchId(Long matchId);
}
