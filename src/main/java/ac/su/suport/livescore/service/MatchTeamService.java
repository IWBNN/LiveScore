package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.constant.MatchStatus;
import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.domain.MatchTeam;
import ac.su.suport.livescore.domain.Team;
import ac.su.suport.livescore.dto.MatchTeamDTO;
import ac.su.suport.livescore.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatchTeamService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchTeamService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public MatchTeamDTO getMatchRecords(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + matchId));

        List<MatchTeam> matchTeams = match.getMatchTeams();
        if (matchTeams.size() != 2) {
            throw new IllegalStateException("Match does not have exactly two teams");
        }

        Team team1 = Optional.ofNullable(matchTeams.get(0).getTeam())
                .orElseThrow(() -> new IllegalStateException("Team 1 is null"));
        Team team2 = Optional.ofNullable(matchTeams.get(1).getTeam())
                .orElseThrow(() -> new IllegalStateException("Team 2 is null"));

        DepartmentEnum team1Department = team1.getDepartment();
        DepartmentEnum team2Department = team2.getDepartment();

        int team1Wins = 0, team1Draws = 0, team1Losses = 0;
        int team2Wins = 0, team2Draws = 0, team2Losses = 0;
        int matchWins = 0, matchDraws = 0, matchLosses = 0;

        List<Match> allMatches = matchRepository.findAll();

        for (Match m : allMatches) {

            if (m.getStatus() != MatchStatus.PAST) {
                continue; // matchStatus가 "PAST"인것만 고려
            }

            List<MatchTeam> mTeams = m.getMatchTeams();
            if (mTeams.size() == 2) {
                Team t1 = mTeams.get(0).getTeam();
                Team t2 = mTeams.get(1).getTeam();

                if (t1 == null || t2 == null) continue;

                Integer score1 = mTeams.get(0).getScore();
                Integer score2 = mTeams.get(1).getScore();

                if (score1 == null || score2 == null) continue;

                if (t1.getDepartment().equals(team1Department)) {
                    if (score1 > score2) team1Wins++;
                    else if (score1.equals(score2)) team1Draws++;
                    else team1Losses++;
                }

                if (t2.getDepartment().equals(team2Department)) {
                    if (score2 > score1) team2Wins++;
                    else if (score2.equals(score1)) team2Draws++;
                    else team2Losses++;
                }

                if (t1.getDepartment().equals(team1Department) && t2.getDepartment().equals(team2Department)) {
                    if (score1 > score2) matchWins++;
                    else if (score1.equals(score2)) matchDraws++;
                    else matchLosses++;
                }
            }
        }

        return new MatchTeamDTO(
                matchId,
                team1Department, team1Wins, team1Draws, team1Losses,
                team2Department, team2Wins, team2Draws, team2Losses,
                matchWins, matchDraws, matchLosses
        );
    }
}