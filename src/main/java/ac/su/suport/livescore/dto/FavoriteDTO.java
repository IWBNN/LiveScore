package ac.su.suport.livescore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FavoriteDTO {
    private Long favoriteId;
    private Long userId;
    private Long matchId;
}
