package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.UserRole;
import ac.su.suport.livescore.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class BanService {

    private final RedisTemplate<String, Integer> redisTemplate; // Redis와의 상호작용을 위한 RedisTemplate
    private final ChatRoomRepository chatRoomRepository; // 채팅방 저장소

    public void banUser(String roomId, String userId, UserRole userRole) {
        // ADMIN 권한 검증
        if (userRole != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only ADMIN can ban users."); // ADMIN이 아닌 경우 예외 발생
        }

        String banKey = "ban:" + roomId + ":" + userId; // Redis에 저장될 키 값 설정
        Integer banCount = redisTemplate.opsForValue().get(banKey); // 사용자가 이전에 강퇴된 적이 있는지 확인

        if (banCount == null) {
            // 첫 번째 강퇴: 10분 동안 강퇴
            redisTemplate.opsForValue().set(banKey, 1, 10, TimeUnit.MINUTES); // 첫 번째 강퇴로 10분 동안 강퇴 설정
        } else if (banCount == 1) {
            // 두 번째 강퇴: 영구 강퇴
            redisTemplate.opsForValue().set(banKey, 2); // 두 번째 강퇴로 영구 강퇴 설정
        }
    }

    public boolean isUserBanned(String roomId, String userId) {
        String banKey = "ban:" + roomId + ":" + userId; // Redis에서 사용될 키 값 설정
        Integer banCount = redisTemplate.opsForValue().get(banKey); // 사용자의 강퇴 상태를 확인
        return banCount != null; // banCount가 존재하면 사용자는 강퇴된 상태
    }
}
