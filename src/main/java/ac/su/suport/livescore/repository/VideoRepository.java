package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByMatch(Match match);
}
