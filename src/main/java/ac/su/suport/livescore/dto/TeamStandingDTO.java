package ac.su.suport.livescore.dto;

import ac.su.suport.livescore.constant.DepartmentEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TeamStandingDTO {
    private Long id;
    private DepartmentEnum department;
    private int win;
    private int draw;
    private int lose;
    private int points;
    private List<Long> matchId;

    public TeamStandingDTO(Long id, DepartmentEnum department) {
        this.id = id;
        this.department = department;
        this.win = 0;
        this.draw = 0;
        this.lose = 0;
        this.points = 0;
        this.matchId = new ArrayList<>();
    }

    public int getPoints() {
        return (win * 3) + draw;
    }

    public void incrementWin() {
        this.win++;
    }

    public void incrementDraw() {
        this.draw++;
    }

    public void incrementLose() {
        this.lose++;
    }

    @Override
    public String toString() {
        return "TeamStandingDTO{" +
                "id=" + id +
                ", department=" + department +
                ", win=" + win +
                ", draw=" + draw +
                ", lose=" + lose +
                ", points=" + getPoints() +
                '}';
    }

    public void addMatchId(Long matchId) {
        this.matchId.add(matchId);
    }

    public void updatePoints() {
        this.points = (win * 3) + draw;
    }
}