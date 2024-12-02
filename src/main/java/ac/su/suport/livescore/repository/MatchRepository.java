package ac.su.suport.livescore.repository;

import ac.su.suport.livescore.constant.MatchStatus;
import ac.su.suport.livescore.constant.MatchType;
import ac.su.suport.livescore.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findBySportAndMatchType(String sport, MatchType matchType);

    List<Match> findAllByOrderByDateDesc();

    List<Match> findByStatusAndDateOrderByDateDesc(MatchStatus status, LocalDate date);
    List<Match> findByStatusOrderByDateDesc(MatchStatus status);

    List<Match> findBySportOrderByDateDesc(String sport);

    List<Match> findByDateOrderByDateDesc(LocalDate date);

    List<Match> findBySportAndDateOrderByDateDesc(String sport, LocalDate date);

    Optional<Match> findByRoundAndSport(String round, String sport);

    @Query("SELECT DISTINCT m FROM Match m JOIN m.matchTeams mt JOIN mt.team t WHERE t.department = :department ORDER BY m.date DESC")
    List<Match> findByDepartmentOrderByDateDesc(@Param("department") String department);

    @Query("SELECT DISTINCT m FROM Match m JOIN m.matchTeams mt JOIN mt.team t WHERE m.sport = :sport AND t.department = :department ORDER BY m.date DESC")
    List<Match> findBySportAndDepartmentOrderByDateDesc(@Param("sport") String sport, @Param("department") String department);

    @Query("SELECT DISTINCT m FROM Match m JOIN m.matchTeams mt JOIN mt.team t WHERE m.date = :date AND t.department = :department ORDER BY m.date DESC")
    List<Match> findByDateAndDepartmentOrderByDateDesc(@Param("date") LocalDate date, @Param("department") String department);

    @Query("SELECT DISTINCT m FROM Match m JOIN m.matchTeams mt JOIN mt.team t WHERE m.sport = :sport AND m.date = :date AND t.department = :department ORDER BY m.date DESC")
    List<Match> findBySportAndDateAndDepartmentOrderByDateDesc(@Param("sport") String sport, @Param("date") LocalDate date, @Param("department") String department);

    @Query("SELECT m FROM Match m WHERE m.sport = :sport AND m.matchType = :matchType ORDER BY m.round DESC")
    List<Match> findBySportAndMatchTypeOrderByRoundDesc(@Param("sport") String sport, @Param("matchType") MatchType matchType);

}
