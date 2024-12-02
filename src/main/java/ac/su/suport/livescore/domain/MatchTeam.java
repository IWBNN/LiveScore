package ac.su.suport.livescore.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "match_team", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "team_id"})
})
@Getter @Setter
@AllArgsConstructor
public class MatchTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_team_id")
    private Long matchTeamId;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "lineup_id")
    private Lineup lineup;

    @Column(name = "score")
    private Integer score;

    @Column(name = "sub_score")
    private String subScores;

    public MatchTeam(Match match, Team team, int score) {
        this.match = match;
        this.team = team;
        this.score = score;
    }

    public MatchTeam(){

    }
}