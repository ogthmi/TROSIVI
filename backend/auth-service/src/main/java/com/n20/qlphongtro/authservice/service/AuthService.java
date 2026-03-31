package com.n20.qlphongtro.authservice.service;

import com.n20.qlphongtro.authservice.model.User;
import com.n20.qlphongtro.authservice.exception.AuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final WebClient.Builder webClientBuilder;

    private User getUserByUserName(String username){
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("lb://user-service/api/user/internal/username/{username}", username)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    private User getUserById(Long userId){
        WebClient webClient = webClientBuilder.build();

        return webClient.get()
                .uri("lb://user-service/api/user/internal/id/{userId}", userId)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    public Map<String, Object> checkSignIn(String username, String password) {
        var user = getUserByUserName(username);

        if (user != null && !BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthException("Password mismatched");
        }

        assert user != null;
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken,
                "user", user
        );
    }

    public Map<String, Object> refreshAccessToken(String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AuthException("Refresh token invalid or expired");
        }

        String userId = jwtService.getUserIdFromToken(refreshToken, jwtService.getRefreshKey());

        User user = getUserById(Long.parseLong(userId));

        String newAccessToken = jwtService.generateAccessToken(user);

        String newRefreshToken = jwtService.generateRefreshToken(user);

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}