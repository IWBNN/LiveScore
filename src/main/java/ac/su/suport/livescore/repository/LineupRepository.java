package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.domain.Lineup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineupRepository extends JpaRepository<Lineup, Long> {
    List<Lineup> findByTeamTeamId(Long teamId);
}
