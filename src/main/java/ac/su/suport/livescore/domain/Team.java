package ac.su.suport.livescore.domain;

import ac.su.suport.livescore.constant.DepartmentEnum;
import ac.su.suport.livescore.domain.Lineup;
import ac.su.suport.livescore.domain.MatchTeam;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@Entity
@Table(name = "teams", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"department"})})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long teamId;

    @Column(name = "team_name")
    private String teamName;

    @Enumerated(EnumType.STRING)
    @Column(name = "department",length = 50)
    private DepartmentEnum department;

    @Column(name = "score")
    private Integer score = 0;  // Initialize with 0

    @Column(name = "team_point")
    private Integer teamPoint = 0;  // Initialize with 0

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lineup> lineups;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchTeam> matchTeams;
}