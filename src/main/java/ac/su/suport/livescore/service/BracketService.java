package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.constant.MatchStatus;
import ac.su.suport.livescore.constant.MatchType;
import ac.su.suport.livescore.constant.TournamentRound;
import ac.su.suport.livescore.domain.Match;
import ac.su.suport.livescore.domain.MatchTeam;
import ac.su.suport.livescore.domain.Team;
import ac.su.suport.livescore.dto.*;
import ac.su.suport.livescore.repository.MatchRepository;
import ac.su.suport.livescore.repository.MatchTeamRepository;
import ac.su.suport.livescore.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.apache.kafka.shaded.com.google.protobuf.ServiceException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BracketService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchTeamRepository matchTeamRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    @Retryable(
            value = { OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void deleteTournamentBracket(Long id) {
        String lockKey = "lock:tournament:" + id;
        boolean locked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(30)));

        if (!locked) {
            throw new ConcurrentModificationException("Tournament match is being modified by another operation");
        }

        try {
            Match match = matchRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tournament match not found with id: " + id));

            List<Match> relatedMatches = findRelatedMatches(match);

            // Delete in reverse order (from child to parent)
            for (int i = relatedMatches.size() - 1; i >= 0; i--) {
                Match relatedMatch = relatedMatches.get(i);
                deleteMatch(relatedMatch);
            }

            log.info("Successfully deleted tournament match and related matches for id: {}", id);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    private List<Match> findRelatedMatches(Match match) {
        List<Match> relatedMatches = new ArrayList<>();
        relatedMatches.add(match);

        while (match.getPreviousMatchId() != null) {
            match = matchRepository.findById(match.getPreviousMatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Related match not found"));
            relatedMatches.add(match);
        }

        return relatedMatches;
    }

    private void deleteMatch(Match match) {
        matchTeamRepository.deleteAll(match.getMatchTeams());
        matchRepository.delete(match);
        log.debug("Deleted match: {}", match.getMatchId());
    }


    @Recover
    public void recoverDeleteBracket(OptimisticLockingFailureException e, Long id) throws ServiceException {
        log.error("Failed to delete bracket after 3 attempts: {}", id, e);
        throw new ServiceException("Failed to delete bracket due to concurrent modifications", e);
    }

    public Map<String, List<GroupDTO>> getSportLeagueBrackets(String sport) {
        List<Match> matches = matchRepository.findBySportAndMatchType(sport, MatchType.LEAGUE);
        Map<String, List<Team>> teamsByGroup = groupTeamsByGroup(matches);
        Map<String, List<GroupDTO>> result = new HashMap<>();

        for (Map.Entry<String, List<Team>> entry : teamsByGroup.entrySet()) {
            String groupName = entry.getKey();
            List<Team> teamsInGroup = entry.getValue();

            GroupDTO groupDTO = new GroupDTO();
            groupDTO.setGroup(groupName);
            groupDTO.setTeams(calculateStandings(teamsInGroup, matches));

            result.computeIfAbsent(groupName, k -> new ArrayList<>()).add(groupDTO);
        }

        return result;
    }

    private Map<String, List<Team>> groupTeamsByGroup(List<Match> matches) {
        Map<String, List<Team>> groupedTeams = new HashMap<>();
        for (Match match : matches) {
            String groupName = match.getGroupName();
            for (MatchTeam matchTeam : match.getMatchTeams()) {
                Team team = matchTeam.getTeam();
                groupedTeams.computeIfAbsent(groupName, k -> new ArrayList<>()).add(team);
            }
        }
        return groupedTeams;
    }


    @Transactional
    public BracketDTO createLeagueBracket(BracketDTO bracketDTO) {
        Match match = convertToMatch(bracketDTO);
        match.setMatchTeams(new ArrayList<>());

        Team teamOne = teamRepository.findByDepartment(bracketDTO.getTeamOneName())
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + bracketDTO.getTeamOneName()));
        Team teamTwo = teamRepository.findByDepartment(bracketDTO.getTeamTwoName())
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + bracketDTO.getTeamTwoName()));

        MatchTeam matchTeamOne = new MatchTeam();
        matchTeamOne.setMatch(match);
        matchTeamOne.setTeam(teamOne);
        matchTeamOne.setScore(bracketDTO.getTeamOneScore());
        match.getMatchTeams().add(matchTeamOne);

        MatchTeam matchTeamTwo = new MatchTeam();
        matchTeamTwo.setMatch(match);
        matchTeamTwo.setTeam(teamTwo);
        matchTeamTwo.setScore(bracketDTO.getTeamTwoScore());
        match.getMatchTeams().add(matchTeamTwo);

        match = matchRepository.save(match);

        return convertToBracketDTO(match);
    }

    @Transactional
    public BracketDTO updateLeagueBracket(Long id, BracketDTO bracketDTO) {
        Match match = matchRepository.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
        updateMatchFromDTO(match, bracketDTO);

        List<MatchTeam> matchTeams = match.getMatchTeams();
        if (matchTeams.size() >= 2) {
            MatchTeam teamOne = matchTeams.get(0);
            MatchTeam teamTwo = matchTeams.get(1);

            teamOne.setScore(bracketDTO.getTeamOneScore());
            teamTwo.setScore(bracketDTO.getTeamTwoScore());

            matchTeamRepository.save(teamOne);
            matchTeamRepository.save(teamTwo);

            if (match.getStatus() == MatchStatus.PAST) {
                updateTeamStandings(teamOne, teamTwo);
            }
        }

        match = matchRepository.save(match);
        return convertToBracketDTO(match);
    }

    private void updateTeamStandings(MatchTeam teamOne, MatchTeam teamTwo) {
        Team team1 = teamOne.getTeam();
        Team team2 = teamTwo.getTeam();

        int scoreOne = teamOne.getScore();
        int scoreTwo = teamTwo.getScore();

        if (scoreOne > scoreTwo) {
            team1.setTeamPoint(team1.getTeamPoint() + 3);
        } else if (scoreOne < scoreTwo) {
            team2.setTeamPoint(team2.getTeamPoint() + 3);
        } else {
            team1.setTeamPoint(team1.getTeamPoint() + 1);
            team2.setTeamPoint(team2.getTeamPoint() + 1);
        }

        teamRepository.save(team1);
        teamRepository.save(team2);
    }

    @Transactional
    @Retryable(
            value = { OptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void deleteLeagueBracket(Long id) {
        String lockKey = "lock:league:" + id;
        boolean locked = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(30)));

        if (!locked) {
            throw new ConcurrentModificationException("League match is being modified by another operation");
        }

        try {
            Match match = matchRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("League match not found with id: " + id));

            // Delete associated MatchTeams
            matchTeamRepository.deleteAll(match.getMatchTeams());

            // Delete the match
            matchRepository.delete(match);

            log.info("Successfully deleted league match with id: {}", id);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Transactional
    public BracketDTO createTournamentBracket(BracketDTO bracketDTO) {
        Match match = convertToMatch(bracketDTO);
        match.setMatchTeams(new ArrayList<>());

        Team teamOne = findOrCreateTeam(bracketDTO.getTeamOneName());
        Team teamTwo = findOrCreateTeam(bracketDTO.getTeamTwoName());

        MatchTeam matchTeamOne = new MatchTeam(match, teamOne, bracketDTO.getTeamOneScore());
        matchTeamOne.setSubScores(bracketDTO.getTeamOneSubScores());

        MatchTeam matchTeamTwo = new MatchTeam(match, teamTwo, bracketDTO.getTeamTwoScore());
        matchTeamTwo.setSubScores(bracketDTO.getTeamTwoSubScores());

        match.getMatchTeams().add(matchTeamOne);
        match.getMatchTeams().add(matchTeamTwo);

        match = matchRepository.save(match);
        return convertToBracketDTO(match);
    }

    @Transactional
    public BracketDTO updateTournamentBracket(Long id, BracketDTO bracketDTO) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        updateMatchFromDTO(match, bracketDTO);
        matchRepository.save(match);
        updateMatchTeams(match, bracketDTO);
        if (match.getStatus() == MatchStatus.PAST) {
            updateStandings(match);
            List<TournamentMatchDTO> updatedBrackets = getSportTournamentBrackets(match.getSport());
            createNextRoundMatchesIfNeeded(updatedBrackets, match.getSport());
            updateNextMatchIds(updatedBrackets);
        }
        return convertToBracketDTO(match);
    }




    private Match convertToMatch(BracketDTO bracketDTO) {
        Match match = new Match();
        updateMatchFromDTO(match, bracketDTO);
        return match;
    }

    private void updateMatchFromDTO(Match match, BracketDTO bracketDTO) {
        match.setSport(bracketDTO.getSports());
        match.setDate(bracketDTO.getMatchDate());
        match.setStartTime(bracketDTO.getStartTime());
        match.setMatchType(bracketDTO.getMatchType());
        match.setStatus(bracketDTO.getMatchStatus());
        match.setGroupName(bracketDTO.getGroupName());
        match.setRound(bracketDTO.getRound());
    }

    private Team findOrCreateTeam(DepartmentEnum department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        return teamRepository.findByDepartment(department)
                .orElseGet(() -> {
                    Team newTeam = new Team();
                    newTeam.setDepartment(department);
                    newTeam.setTeamName(department.name());
                    newTeam.setTeamPoint(0);
                    newTeam.setScore(0);
                    return teamRepository.save(newTeam);
                });
    }

    private void updateMatchTeams(Match match, BracketDTO bracketDTO) {
        List<MatchTeam> matchTeams = match.getMatchTeams();

        if (matchTeams.size() >= 2) {
            MatchTeam matchTeamOne = matchTeams.get(0);
            MatchTeam matchTeamTwo = matchTeams.get(1);

            Team teamOne = findOrCreateTeam(bracketDTO.getTeamOneName());
            Team teamTwo = findOrCreateTeam(bracketDTO.getTeamTwoName());

            matchTeamOne.setTeam(teamOne);
            matchTeamOne.setScore(bracketDTO.getTeamOneScore());
            matchTeamOne.setSubScores(bracketDTO.getTeamOneSubScores());

            matchTeamTwo.setTeam(teamTwo);
            matchTeamTwo.setScore(bracketDTO.getTeamTwoScore());
            matchTeamTwo.setSubScores(bracketDTO.getTeamTwoSubScores());

            matchTeamRepository.save(matchTeamOne);
            matchTeamRepository.save(matchTeamTwo);
        }
    }

    private void updateStandings(Match match) {
        List<MatchTeam> matchTeams = match.getMatchTeams();

        if (matchTeams.size() >= 2) {
            MatchTeam matchTeamOne = matchTeams.get(0);
            MatchTeam matchTeamTwo = matchTeams.get(1);

            Team teamOne = matchTeamOne.getTeam();
            Team teamTwo = matchTeamTwo.getTeam();

            if (matchTeamOne.getScore() > matchTeamTwo.getScore()) {
                teamOne.setTeamPoint(teamOne.getTeamPoint() + 3);
            } else if (matchTeamOne.getScore() < matchTeamTwo.getScore()) {
                teamTwo.setTeamPoint(teamTwo.getTeamPoint() + 3);
            } else {
                teamOne.setTeamPoint(teamOne.getTeamPoint() + 1);
                teamTwo.setTeamPoint(teamTwo.getTeamPoint() + 1);
            }

            teamRepository.save(teamOne);
            teamRepository.save(teamTwo);
        }
    }

    private List<TeamStandingDTO> calculateStandings(List<Team> teamsInGroup, List<Match> allMatches) {
        Map<Long, TeamStandingDTO> standings = new HashMap<>();

        for (Team team : teamsInGroup) {
            standings.put(team.getTeamId(), new TeamStandingDTO(team.getTeamId(), team.getDepartment()));
        }

        for (Match match : allMatches) {
            List<MatchTeam> matchTeams = match.getMatchTeams();
            if (matchTeams.size() >= 2) {
                MatchTeam teamOne = matchTeams.get(0);
                MatchTeam teamTwo = matchTeams.get(1);

                if (standings.containsKey(teamOne.getTeam().getTeamId()) &&
                        standings.containsKey(teamTwo.getTeam().getTeamId())) {
                    TeamStandingDTO standingOne = standings.get(teamOne.getTeam().getTeamId());
                    TeamStandingDTO standingTwo = standings.get(teamTwo.getTeam().getTeamId());

                    standingOne.addMatchId(match.getMatchId());
                    standingTwo.addMatchId(match.getMatchId());

                    if (match.getStatus() == MatchStatus.PAST) {
                        updateStandings(teamOne, teamTwo, standingOne, standingTwo);
                    }
                }
            }
        }

        return new ArrayList<>(standings.values());
    }

    private void updateStandings(MatchTeam teamOne, MatchTeam teamTwo,
                                 TeamStandingDTO standingOne, TeamStandingDTO standingTwo) {
        int scoreOne = teamOne.getScore();
        int scoreTwo = teamTwo.getScore();

        if (scoreOne > scoreTwo) {
            standingOne.setWin(standingOne.getWin() + 1);
            standingTwo.setLose(standingTwo.getLose() + 1);
        } else if (scoreOne < scoreTwo) {
            standingTwo.setWin(standingTwo.getWin() + 1);
            standingOne.setLose(standingOne.getLose() + 1);
        } else {
            standingOne.setDraw(standingOne.getDraw() + 1);
            standingTwo.setDraw(standingTwo.getDraw() + 1);
        }

        standingOne.updatePoints();
        standingTwo.updatePoints();
    }






    private BracketDTO convertToBracketDTO(Match match) {
        BracketDTO bracketDTO = new BracketDTO();
        bracketDTO.setMatchId(match.getMatchId());
        bracketDTO.setSports(match.getSport());
        bracketDTO.setMatchDate(match.getDate());
        bracketDTO.setStartTime(match.getStartTime());
        bracketDTO.setMatchType(match.getMatchType());
        bracketDTO.setMatchStatus(match.getStatus());
        bracketDTO.setGroupName(match.getGroupName());
        bracketDTO.setRound(match.getRound());

        List<MatchTeam> matchTeams = match.getMatchTeams();
        if (matchTeams != null && matchTeams.size() >= 2) {
            MatchTeam teamOne = matchTeams.get(0);
            MatchTeam teamTwo = matchTeams.get(1);

            bracketDTO.setTeamOneName(teamOne.getTeam().getDepartment());
            bracketDTO.setTeamTwoName(teamTwo.getTeam().getDepartment());
            bracketDTO.setTeamOneScore(teamOne.getScore());
            bracketDTO.setTeamTwoScore(teamTwo.getScore());
            bracketDTO.setTeamOneSubScores(teamOne.getSubScores());
            bracketDTO.setTeamTwoSubScores(teamTwo.getSubScores());

            // 서브스코어 표시 여부 설정
            bracketDTO.setShowSubScores(match.getSport().equals("SOCCER") ?
                    (teamOne.getSubScores() != null && !teamOne.getSubScores().isEmpty()) : true);
        }

        return bracketDTO;
    }

    private void updateNextMatchIds(List<TournamentMatchDTO> matches) {
        Map<String, List<TournamentMatchDTO>> roundMatches = matches.stream()
                .collect(Collectors.groupingBy(TournamentMatchDTO::getTournamentRoundText));

        List<TournamentRound> rounds = Arrays.asList(TournamentRound.values());
        for (int i = 0; i < rounds.size() - 1; i++) {
            TournamentRound currentRound = rounds.get(i);
            TournamentRound nextRound = rounds.get(i + 1);

            List<TournamentMatchDTO> currentMatches = roundMatches.get(currentRound.name());
            List<TournamentMatchDTO> nextMatches = roundMatches.get(nextRound.name());

            if (currentMatches != null && nextMatches != null) {
                for (int j = 0; j < currentMatches.size(); j += 2) {
                    if (j / 2 < nextMatches.size()) {
                        Long nextMatchId = nextMatches.get(j / 2).getId();
                        currentMatches.get(j).setNextMatchId(nextMatchId);
                        if (j + 1 < currentMatches.size()) {
                            currentMatches.get(j + 1).setNextMatchId(nextMatchId);
                        }
                        updateNextMatchId(currentMatches.get(j).getId(), nextMatchId);
                        if (j + 1 < currentMatches.size()) {
                            updateNextMatchId(currentMatches.get(j + 1).getId(), nextMatchId);
                        }
                    }
                }
            }
        }
    }

    private void updateNextMatchId(Long currentMatchId, Long nextMatchId) {
        log.info("Updating nextMatchId for match {} to {}", currentMatchId, nextMatchId);
        Match currentMatch = matchRepository.findById(currentMatchId)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + currentMatchId));
        currentMatch.setNextMatchId(nextMatchId);
        Match updatedMatch = matchRepository.save(currentMatch);
        log.info("Updated match in database: {}", updatedMatch);

    }



    public List<TournamentMatchDTO> getSportTournamentBrackets(String sport) {
        List<Match> matches = matchRepository.findBySportAndMatchTypeOrderByRoundDesc(sport, MatchType.TOURNAMENT);
        List<TournamentMatchDTO> dtos = matches.stream()
                .map(this::convertToTournamentMatchDTO)
                .collect(Collectors.toList());

        log.info("Initial tournament brackets for sport {}: {}", sport, dtos);
        createNextRoundMatchesIfNeeded(dtos, sport);
        log.info("Tournament brackets after creating next round for sport {}: {}", sport, dtos);
        updateNextMatchIds(dtos);
        log.info("Final tournament brackets for sport {}: {}", sport, dtos);
        return dtos;
    }

    private void createNextRoundMatchesIfNeeded(List<TournamentMatchDTO> matches, String sport) {
        Map<String, List<TournamentMatchDTO>> roundMatches = matches.stream()
                .collect(Collectors.groupingBy(TournamentMatchDTO::getTournamentRoundText));

        List<TournamentRound> rounds = Arrays.asList(TournamentRound.values());
        for (int i = 0; i < rounds.size() - 1; i++) {
            TournamentRound currentRound = rounds.get(i);
            TournamentRound nextRound = rounds.get(i + 1);

            List<TournamentMatchDTO> currentMatches = roundMatches.get(currentRound.name());
            List<TournamentMatchDTO> nextMatches = roundMatches.get(nextRound.name());

            if (currentMatches != null && (nextMatches == null || nextMatches.isEmpty())) {
                boolean allMatchesFinished = currentMatches.stream()
                        .allMatch(match -> "PAST".equals(match.getState()));
                int expectedMatches = getCurrentRoundExpectedMatches(currentRound);

                log.info("Current round: {}, Finished matches: {}, Expected matches: {}",
                        currentRound, currentMatches.size(), expectedMatches);

                if (allMatchesFinished && currentMatches.size() == expectedMatches) {
                    log.info("Creating next round matches for round: {} in sport: {}", nextRound, sport);
                    List<TournamentMatchDTO> newMatches = createNextRoundMatches(currentMatches, nextRound, sport);
                    matches.addAll(newMatches);
                    roundMatches.put(nextRound.name(), newMatches);
                }
            }
        }
    }

    private int getCurrentRoundExpectedMatches(TournamentRound round) {
        switch (round) {
            case QUARTER_FINALS: return 4;
            case SEMI_FINALS: return 2;
            case FINAL: return 1;
            default: throw new IllegalArgumentException("Unknown round: " + round);
        }
    }

    private List<TournamentMatchDTO> createNextRoundMatches(List<TournamentMatchDTO> currentMatches, TournamentRound nextRound, String sport) {
        List<TournamentMatchDTO> newMatches = new ArrayList<>();
        for (int i = 0; i < currentMatches.size(); i += 2) {
            TournamentMatchDTO match1 = currentMatches.get(i);
            TournamentMatchDTO match2 = i + 1 < currentMatches.size() ? currentMatches.get(i + 1) : null;

            ParticipantDTO winner1 = getWinner(match1);
            ParticipantDTO winner2 = match2 != null ? getWinner(match2) : null;

            if (winner1 != null && winner2 != null) {
                Match newMatch = new Match();
                newMatch.setSport(sport);
                newMatch.setMatchType(MatchType.TOURNAMENT);
                newMatch.setRound(nextRound.name());
                newMatch.setStatus(MatchStatus.FUTURE);
                newMatch.setDate(LocalDate.now().plusDays(7));
                newMatch.setStartTime(LocalTime.of(18, 0));
                newMatch.setPreviousMatchId(match1.getId());  // 이전 매치 ID 설정

                newMatch = matchRepository.save(newMatch);
                log.info("Created new match: {}", newMatch);

                // 이전 매치들의 nextMatchId 업데이트
                updateNextMatchId(match1.getId(), newMatch.getMatchId());
                if (match2 != null) {
                    updateNextMatchId(match2.getId(), newMatch.getMatchId());
                }

                createMatchTeam(newMatch, winner1, true);
                createMatchTeam(newMatch, winner2, true);
                TournamentMatchDTO newMatchDTO = convertToTournamentMatchDTO(newMatch);
                newMatches.add(newMatchDTO);
            }
        }
        return currentMatches;
    }

    private ParticipantDTO getWinner(TournamentMatchDTO match) {
        return match.getParticipants().stream()
                .filter(ParticipantDTO::getWinner)
                .findFirst()
                .orElse(null);
    }

    private void createMatchTeam(Match match, ParticipantDTO participant, boolean isWinner) {
        Team team = teamRepository.findById(Long.parseLong(participant.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));

        MatchTeam matchTeam = new MatchTeam();
        matchTeam.setMatch(match);
        matchTeam.setTeam(team);
        matchTeam.setScore(0);  // 초기 점수를 0으로 설정

        matchTeamRepository.save(matchTeam);
    }

    private TournamentMatchDTO convertToTournamentMatchDTO(Match match) {
        TournamentMatchDTO dto = new TournamentMatchDTO();
        dto.setId(match.getMatchId());
        dto.setName(match.getRound() + " - Match " + match.getMatchId());
        dto.setNextMatchId(match.getNextMatchId());
        dto.setTournamentRoundText(match.getRound());
        dto.setStartTime(match.getDate().atTime(match.getStartTime()).toString());
        dto.setState(match.getStatus().toString());

        // match.getMatchTeams()가 null인지 체크하고, null일 경우 빈 리스트로 처리
        List<ParticipantDTO> participants = Optional.ofNullable(match.getMatchTeams())
                .orElse(Collections.emptyList())  // null인 경우 빈 리스트 사용
                .stream()
                .map(this::convertToParticipantDTO)
                .collect(Collectors.toList());
        dto.setParticipants(participants);

        return dto;
    }


    private ParticipantDTO convertToParticipantDTO(MatchTeam matchTeam) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(matchTeam.getTeam().getTeamId().toString());
        dto.setName(matchTeam.getTeam().getDepartment().getKoreanName());
        dto.setImage("userImage");
        dto.setSubScore(matchTeam.getSubScores());

        if (matchTeam.getMatch().getStatus() == MatchStatus.FUTURE) {
            dto.setResultText("0");  // 초기값을 0으로 설정
            dto.setStatus("FUTURE");
            dto.setWinner(null);
        } else {
            dto.setResultText(matchTeam.getScore() != null ? matchTeam.getScore().toString() : "0");
            dto.setStatus(matchTeam.getMatch().getStatus().toString());
            dto.setWinner(determineWinner(matchTeam));
        }

        return dto;
    }


    private Boolean determineWinner(MatchTeam matchTeam) {
        Match match = matchTeam.getMatch();
        if (match.getMatchTeams().size() != 2) {
            return null;  // 경기에 두 팀이 아니면 승자를 결정할 수 없음
        }

        MatchTeam opponent = match.getMatchTeams().stream()
                .filter(mt -> !mt.equals(matchTeam))
                .findFirst()
                .orElse(null);

        if (opponent == null) {
            return null;  // 상대팀을 찾을 수 없음
        }

        // 주 점수 비교
        if (matchTeam.getScore() > opponent.getScore()) {
            return true;
        } else if (matchTeam.getScore() < opponent.getScore()) {
            return false;
        }

        // 주 점수가 같은 경우 서브스코어 비교
        if (matchTeam.getSubScores() != null && opponent.getSubScores() != null) {
            int subScore1 = Integer.parseInt(matchTeam.getSubScores());
            int subScore2 = Integer.parseInt(opponent.getSubScores());
            return subScore1 > subScore2;
        }

        // 모든 점수가 같은 경우
        return null;  // 또는 다른 규칙을 적용할 수 있음 (예: 첫 번째 팀 승리)
    }



}