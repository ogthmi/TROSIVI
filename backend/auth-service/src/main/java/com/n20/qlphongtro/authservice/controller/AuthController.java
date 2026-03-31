package com.n20.qlphongtro.authservice.controller;

import com.n20.qlphongtro.authservice.exception.AuthException;
import com.n20.qlphongtro.authservice.model.User;
import com.n20.qlphongtro.authservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    private Cookie createRefreshCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        return refreshCookie;
    }

    private Cookie createDeleteCookie(){
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setPath("/api/auth/refresh");
        deleteCookie.setMaxAge(0);

        return deleteCookie;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody User request, HttpServletResponse response) {
        var tokens = authService.checkSignIn(request.getUsername(), request.getPassword());

        var refreshCookie = createRefreshCookie((String) tokens.get("refreshToken"));
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of(
                "accessToken", tokens.get("accessToken"),
                "user", tokens.get("user")
        ));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        var deleteCookie = createDeleteCookie();
        response.addCookie(deleteCookie);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) throw new AuthException("No refresh token");

        var tokens = authService.refreshAccessToken(refreshToken);

        var newRefreshCookie = createRefreshCookie((String) tokens.get("refreshToken"));
        response.addCookie(newRefreshCookie);

        return ResponseEntity.ok(Map.of("accessToken", tokens.get("accessToken")));
    }
}