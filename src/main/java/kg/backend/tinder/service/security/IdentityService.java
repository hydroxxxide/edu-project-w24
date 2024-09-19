package kg.backend.tinder.service.security;


import kg.backend.tinder.dto.AuthRequest;
import kg.backend.tinder.dto.AuthResponse;
import kg.backend.tinder.model.CustomUserDetails;
import kg.backend.tinder.model.User;
import kg.backend.tinder.repository.UserRepository;
import kg.backend.tinder.repository.UserRoleRepository;
import kg.backend.tinder.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IdentityService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;

    public AuthResponse registerUser(AuthRequest request) {

        Optional<User> existingUser = userRepository.getByUsername(request.getUsername());

        if (existingUser.isPresent()) {
            throw new RuntimeException(request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRoleRepository.getRoleUser()))
                .build();
        userRepository.save(user);

        sendConfirmationCode(user.getEmail());
        return login(request);
    }


    public AuthResponse login(AuthRequest authRequest) {

        User user = authenticate(authRequest.getUsername(), authRequest.getPassword());

        return new AuthResponse(user.getId(), user.getUsername(),
                jwtService.generateToken(new CustomUserDetails(user)),
                jwtService.generateRefreshToken(new CustomUserDetails(user)),
                user.getRoles());
    }

    public AuthResponse logout(String token) {
        return new AuthResponse(
                Long.parseLong(jwtService.extractUserId(token)),
                jwtService.extractUsername(token),
                jwtService.generateLogoutToken(token),
                null,
                Collections.emptyList());
    }

    public AuthResponse refreshToken(String refreshToken) {

        if (!jwtService.extractExpiration(refreshToken).before(new Date())) {

            String userId = jwtService.extractUserId(refreshToken);
            Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));

            if (userOpt.isPresent()) {
                return new AuthResponse(userOpt.get().getId(), userOpt.get().getUsername(),
                        jwtService.generateToken(new CustomUserDetails(userOpt.get())),
                        jwtService.generateRefreshToken(new CustomUserDetails(userOpt.get())),
                        userOpt.get().getRoles());
            }
        }

        throw new RuntimeException("(Token is not valid)");
    }

    private User authenticate(String username, String password) {

        try {
            log.info("Authenticating user by username: {}", username);

            Optional<User> userOpt = userRepository.getByUsername(username);
            if (userOpt.isPresent())
                if (passwordEncoder.matches(password, userOpt.get().getPassword()))
                    return userOpt.get();

        } catch (Exception e) {
            //ignore
        }

        throw new RuntimeException("(Invalid Username or password!)");
    }


    public void sendResetPasswordLink(String email) {
        User user = userRepository.getByEmail(email).orElseThrow();
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpireTime(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String message = "Токен для сброса пароля: " + resetToken;
        String subject = "Сброс пароля";
        emailService.sendEmail(email, subject, message);
    }

    public void saveNewPassword(String token, String newPassword) {
        User user = userRepository.getUserByResetToken(token).orElseThrow();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpireTime(null);
        userRepository.save(user);
    }


    public void saveNewPassword(long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow();
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        userRepository.save(user);
    }


    public void sendConfirmationCode(String email) {
        User user = userRepository.getByEmail(email).orElseThrow();
        String code = generateCode(6);
        user.setConfirmationCode(code);
        emailService.sendEmail(
                user.getEmail(),
                "Подтвреждение почты",
                "Код подтверждения: " + code);
    }

    public void confirmEmail(long userId, String code) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getConfirmationCode().equals(code)) {
            user.setIsConfirmed(true);
            user.setConfirmationCode(null);
            userRepository.save(user);
        }
    }

    private static String generateCode(int digits) {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < digits; i++) {
            int randomDigit = ThreadLocalRandom.current().nextInt(10);
            code.append(randomDigit);
        }
        return code.toString();
    }
}
