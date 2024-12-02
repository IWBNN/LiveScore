package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.domain.Team;
import ac.su.suport.livescore.dto.TeamDTO;
import ac.su.suport.livescore.dto.TeamDTO.Request;
import ac.su.suport.livescore.dto.TeamDTO.Request;
import ac.su.suport.livescore.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // 모든 팀 정보를 가져오는 메서드
    public List<TeamDTO.Response> getAllTeams() {
        return teamRepository.findAll().stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    // 팀 ID로 팀 정보를 가져오는 메서드
    public Optional<TeamDTO.Response> getTeamById(Long id) {
        return teamRepository.findById(id).map(this::convertToResponseDTO);
    }

    // 팀 삭제
    public boolean deleteTeam(Long id) {
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 팀 생성
    public Request createTeam(Request teamDTO) {
        Team team = convertToEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        return convertToRequestDTO(savedTeam);
    }

    // 팀 정보 수정
    public Optional<Request> updateTeam(Long id, Request teamDTO) {
        return teamRepository.findById(id).map(existingTeam -> {
            existingTeam.setTeamName(teamDTO.getTeamName());
            existingTeam.setDepartment(DepartmentEnum.valueOf(String.valueOf(teamDTO.getDepartment())));
            existingTeam.setTeamPoint(teamDTO.getTeamPoint());
            Team updatedTeam = teamRepository.save(existingTeam);
            return convertToRequestDTO(updatedTeam);
        });
    }

    // TeamDTO.Request -> Team 변환
    private Team convertToEntity(Request teamDTO) {
        Team team = new Team();
        team.setTeamName(teamDTO.getTeamName());
        team.setDepartment(DepartmentEnum.valueOf(String.valueOf(teamDTO.getDepartment())));
        team.setTeamPoint(teamDTO.getTeamPoint());
        return team;
    }

    // Team -> TeamDTO.Request 변환
    private Request convertToRequestDTO(Team team) {
        return new Request(
                team.getTeamName(),
                team.getDepartment(),
                team.getTeamPoint()
        );
    }

    // Team -> TeamDTO.Response 변환
    private TeamDTO.Response convertToResponseDTO(Team team) {
        return new TeamDTO.Response(
                team.getTeamId(),
                team.getTeamName(),
                team.getDepartment(),
                team.getTeamPoint()
        );
    }

}