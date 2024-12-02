package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.domain.Lineup;
import ac.su.suport.livescore.domain.MatchTeam;
import ac.su.suport.livescore.domain.Team;
import ac.su.suport.livescore.dto.LineupDTO;
import ac.su.suport.livescore.dto.LineupGroupDTO;
import ac.su.suport.livescore.dto.PlayerDTO;
import ac.su.suport.livescore.repository.LineupRepository;
import ac.su.suport.livescore.repository.MatchTeamRepository;
import ac.su.suport.livescore.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineupService {

    private final LineupRepository lineupRepository;
    private final TeamRepository teamRepository;
    private final MatchTeamRepository matchTeamRepository;

    @Autowired
    public LineupService(LineupRepository lineupRepository, TeamRepository teamRepository, MatchTeamRepository matchTeamRepository) {
        this.lineupRepository = lineupRepository;
        this.teamRepository = teamRepository;
        this.matchTeamRepository = matchTeamRepository;
    }

    // 기본 형식 (flat)으로 데이터 반환
    public List<LineupDTO.Response> getLineupFlatFormat(Long teamId) {
        List<Lineup> lineups = lineupRepository.findByTeamTeamId(teamId);
        return lineups.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 그룹화된 형식으로 데이터 반환
    public LineupGroupDTO.Response getLineupGroupedFormat(Long teamId) {
        List<Lineup> lineups = lineupRepository.findByTeamTeamId(teamId);

        if (lineups.isEmpty()) {
            throw new EntityNotFoundException("No lineups found for team id: " + teamId);
        }

        // 팀 이름과 부서 정보를 가져오기 위해 첫 번째 Lineup 객체에서 정보 추출
        Team team = lineups.get(0).getTeam();
        String teamName = team.getTeamName();
        DepartmentEnum department = team.getDepartment();

        // 그룹화된 데이터 생성
        List<PlayerDTO.Response> playerDTOs = lineups.stream()
                .map(lineup -> new PlayerDTO.Response(
                        lineup.getLineupId(),
                        lineup.getPlayerName(),
                        lineup.getPosition()
                ))
                .collect(Collectors.toList());

        return new LineupGroupDTO.Response(teamName, department, playerDTOs);
    }


    // 팀 라인업 생성
    public List<LineupDTO.Response> createLineup(Long teamId, List<LineupDTO.Request> lineupDTOs) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        List<Lineup> lineups = lineupDTOs.stream()
                .map(dto -> convertToEntity(dto, team))
                .collect(Collectors.toList());

        List<Lineup> createdLineups = lineupRepository.saveAll(lineups);

        return createdLineups.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Lineup -> LineupDTO 변환
    private LineupDTO.Response convertToResponseDTO(Lineup lineup) {
        LineupDTO.Response dto = new LineupDTO.Response();
        dto.setLineupId(lineup.getLineupId());
        dto.setTeamName(lineup.getTeam().getTeamName());
        dto.setDepartment(String.valueOf(lineup.getTeam().getDepartment()));
        dto.setPlayerName(lineup.getPlayerName());
        dto.setPosition(lineup.getPosition());
        return dto;
    }

    // LineupDTO -> Lineup 엔티티로 변환
    private Lineup convertToEntity(LineupDTO.Request lineupDTO, Team team) {
        Lineup lineup = new Lineup();
        lineup.setTeam(team); // 주어진 팀 객체 사용
        lineup.setPlayerName(lineupDTO.getPlayerName());
        lineup.setPosition(lineupDTO.getPosition());
        return lineup;
    }

    // 팀 라인업 업데이트
    public List<LineupDTO.Response> updateLineup(Long teamId, List<LineupDTO.Request> lineupDTOs) {
        // 팀 정보 확인
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        // 존 라인업 가져오기 및 삭제
        List<Lineup> existingLineups = lineupRepository.findByTeamTeamId(teamId);
        lineupRepository.deleteAll(existingLineups);

        // 새로운 라인업 엔티티로 변환 후 저장
        List<Lineup> newLineups = lineupDTOs.stream()
                .map(dto -> convertToEntity(dto, team))
                .collect(Collectors.toList());

        List<Lineup> savedLineups = lineupRepository.saveAll(newLineups);

        // DTO로 변환하여 반환
        return savedLineups.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 팀 라인업 삭제
    // 팀 라인업 삭제
    @Transactional
    public void deleteLineup(Long teamId) {
        List<Lineup> lineups = lineupRepository.findByTeamTeamId(teamId);

        for (Lineup lineup : lineups) {
            List<MatchTeam> matchTeams = matchTeamRepository.findByLineupLineupId(lineup.getLineupId());
            matchTeamRepository.deleteAll(matchTeams);
        }

        lineupRepository.deleteAll(lineups);
    }
}
