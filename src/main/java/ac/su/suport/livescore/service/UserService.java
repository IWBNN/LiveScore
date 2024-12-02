package ac.su.suport.livescore.service;

import ac.su.suport.livescore.constant.UserRole;
import ac.su.suport.livescore.domain.User;
import ac.su.suport.livescore.dto.UserDTO;
import ac.su.suport.livescore.exception.CustomException;
import ac.su.suport.livescore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final Map<String, String> emailVerificationCodes = new HashMap<>();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isNicknameTaken(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    public User registerUser(UserDTO userDTO) {
        if (isEmailTaken(userDTO.getEmail())) {
            throw new CustomException("이미 사용 중인 이메일입니다.");
        }
        if (isNicknameTaken(userDTO.getNickname())) {
            throw new CustomException("이미 사용 중인 닉네임입니다.");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setNickname(userDTO.getNickname());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public void sendVerificationCode(String email) throws Exception {
        if (isEmailTaken(email)) {
            throw new CustomException("이미 사용 중인 이메일입니다.");
        }
        String code = generateVerificationCode();
        emailVerificationCodes.put(email, code);
        String subject = "이메일 인증 코드";
        String text = "이메일 인증 코드는 " + code + " 입니다.";
        emailService.sendVerificationEmail(email, subject, text);
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(emailVerificationCodes.get(email));
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public User authenticate(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            return null;
        }
        return optionalUser.get();
    }

    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException("Email not found"));

        String newPassword = generateRandomPassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        sendNewPasswordEmail(user.getEmail(), newPassword);
    }

    private String generateRandomPassword() {
        Random random = new Random();
        int length = 8;
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private void sendNewPasswordEmail(String to, String newPassword) {
        String subject = "새로운 비밀번호";
        String text = "새로운 비밀번호 : " + newPassword;
        try {
            emailService.sendVerificationEmail(to, subject, text);
        } catch (Exception e) {
            throw new CustomException("이메일 전송 실패");
        }
    }

    public void updateUser(UserDTO userDTO, User currentUser) {
        if (StringUtils.hasText(userDTO.getNickname()) && !userDTO.getNickname().equals(currentUser.getNickname())) {
            if (isNicknameTaken(userDTO.getNickname())) {
                throw new CustomException("이미 사용 중인 닉네임입니다.");
            }
            currentUser.setNickname(userDTO.getNickname());
        }

        if (StringUtils.hasText(userDTO.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(currentUser);
    }

    public void deleteUser(User currentUser) {
        userRepository.delete(currentUser);
    }
}
