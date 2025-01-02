package com.project.popupmarket.service.tokenService;

import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public String createNewAccessToken(String jwtToken) {
        if (!tokenProvider.validToken(jwtToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = jwtTokenService.findByJwtToken(jwtToken).getUserId();
        User user = userService.findById(userId);
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}