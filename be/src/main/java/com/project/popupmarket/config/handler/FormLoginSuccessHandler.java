package com.project.popupmarket.config.handler;

import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.JwtTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FormLoginSuccessHandler extends BaseAuthenticationSuccessHandler {
    public FormLoginSuccessHandler(TokenProvider tokenProvider, JwtTokenRepository jwtTokenRepository) {
        super(tokenProvider, jwtTokenRepository);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();

        String jwtToken = tokenProvider.generateToken(user, JWT_TOKEN_DURATION);
        saveJwtToken(user.getId(), jwtToken);
        addJwtTokenToCookie(request, response, jwtToken);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
