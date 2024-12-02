package ac.su.suport.livescore.controller;

import ac.su.suport.livescore.domain.User;
import ac.su.suport.livescore.dto.FavoriteDTO;
import ac.su.suport.livescore.logger.UserLogger;  // UserLogger 추가
import ac.su.suport.livescore.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // 사용자의 모든 즐겨찾기 목록을 조회합니다.
    @GetMapping
    public List<FavoriteDTO> getFavorites(HttpServletRequest request) {
        Long userId = ((User) request.getSession().getAttribute("currentUser")).getUserId();
        List<FavoriteDTO> favorites = favoriteService.getFavoritesByUserId(userId);

        // 사용자 로깅 추가: 즐겨찾기 조회
        UserLogger.logRequest("i", "즐겨찾기 조회", "/api/favorites", "GET", userId.toString(), "User ID: " + userId + " retrieved favorites", request);

        return favorites;
    }

    // 사용자의 즐겨찾기에 특정 경기를 추가합니다.
    @PostMapping("/{matchId}")
    public FavoriteDTO addFavorite(@PathVariable Long matchId, HttpServletRequest request) {
        Long userId = ((User) request.getSession().getAttribute("currentUser")).getUserId();
        FavoriteDTO addedFavorite = favoriteService.addFavorite(userId, matchId);

        // 사용자 로깅 추가: 즐겨찾기 추가
        UserLogger.logRequest("o", "즐겨찾기 추가", "/api/favorites/" + matchId, "POST", userId.toString(), "User ID: " + userId + " added favorite for Match ID: " + matchId, request);

        return addedFavorite;
    }

    // 사용자의 즐겨찾기에서 특정 항목을 삭제합니다.
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long favoriteId, HttpServletRequest request) {
        Long userId = ((User) request.getSession().getAttribute("currentUser")).getUserId();
        favoriteService.removeFavorite(favoriteId);

        // 사용자 로깅 추가: 즐겨찾기 삭제
        UserLogger.logRequest("o", "즐겨찾기 삭제", "/api/favorites/" + favoriteId, "DELETE", userId.toString(), "User ID: " + userId + " removed favorite with ID: " + favoriteId, request);

        return ResponseEntity.noContent().build();
    }
}
