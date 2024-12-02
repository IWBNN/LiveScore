package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.domain.MatchTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchTeamRepository extends JpaRepository<MatchTeam, Long> {
    List<MatchTeam> findByLineupLineupId(Long lineupId);
}
