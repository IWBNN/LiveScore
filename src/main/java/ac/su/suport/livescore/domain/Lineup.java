package ac.su.suport.livescore.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lineups")
@Getter @Setter
public class Lineup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lineup_id")
    private Long lineupId;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "position")
    private String position;
}