package ac.su.suport.livescore.constant;

import lombok.Getter;

@Getter
public enum TournamentRound {
    QUARTER_FINALS("Quarter-finals"),
    SEMI_FINALS("Semi-finals"),
    FINAL("Final");

    private final String displayName;

    TournamentRound(String displayName) {
        this.displayName = displayName;
    }

}