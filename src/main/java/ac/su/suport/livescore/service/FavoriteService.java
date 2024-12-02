package ac.su.suport.livescore.service;

import ac.su.suport.livescore.domain.Favorite;
import ac.su.suport.livescore.dto.FavoriteDTO;
import ac.su.suport.livescore.repository.FavoriteRepository;
import ac.su.suport.livescore.repository.UserRepository;
import ac.su.suport.livescore.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    public List<FavoriteDTO> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public FavoriteDTO addFavorite(Long userId, Long matchId) {
        Favorite favorite = new Favorite();
        favorite.setUser(userRepository.findById(userId).orElseThrow());
        favorite.setMatch(matchRepository.findById(matchId).orElseThrow());
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return toDTO(savedFavorite);
    }

    public void removeFavorite(Long favoriteId) {
        favoriteRepository.deleteById(favoriteId);
    }

    private FavoriteDTO toDTO(Favorite favorite) {
        FavoriteDTO favoriteDTO = new FavoriteDTO();
        favoriteDTO.setFavoriteId(favorite.getFavoriteId());
        favoriteDTO.setUserId(favorite.getUser().getUserId());
        favoriteDTO.setMatchId(favorite.getMatch().getMatchId());
        return favoriteDTO;
    }
}