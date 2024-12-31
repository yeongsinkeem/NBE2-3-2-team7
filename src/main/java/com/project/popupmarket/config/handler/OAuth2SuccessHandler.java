package com.project.popupmarket.config.handler;

import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.JwtTokenRepository;
import com.project.popupmarket.service.userService.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends BaseAuthenticationSuccessHandler {
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;
    public static final String REGISTER_PATH = "/register";

    public OAuth2SuccessHandler(TokenProvider tokenProvider,
                                JwtTokenRepository jwtTokenRepository,
                                OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository,
                                UserService userService) {
        super(tokenProvider, jwtTokenRepository);
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        // 기존 회원인지 확인
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            processOAuth2Login(request, response, userOptional.get());
        } else {
            String name = (String) oAuth2User.getAttributes().get("name");
            redirectToRegister(request, response, email, name);
        }
    }

    private void processOAuth2Login(HttpServletRequest request, HttpServletResponse response,
                                    User user) throws IOException {
        String jwtToken = tokenProvider.generateToken(user, JWT_TOKEN_DURATION);
        saveJwtToken(user.getId(), jwtToken);
        addJwtTokenToCookie(request, response, jwtToken);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void redirectToRegister(HttpServletRequest request, HttpServletResponse response,
                                    String email, String name) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(REGISTER_PATH)
                .queryParam("email", email)
                .queryParam("name", name)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}