package com.project.popupmarket.config.handler;

import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.project.popupmarket.entity.JwtToken;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.JwtTokenRepository;
import com.project.popupmarket.service.userService.UserService;
import com.project.popupmarket.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String JWT_TOKEN_COOKIE_NAME = "jwt_token";
    public static final Duration JWT_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/main";
    public static final String REGISTER_PATH = "/register";

    private final TokenProvider tokenProvider;
    private final JwtTokenRepository jwtTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");

        try {
            // 기존 사용자인지 확인
            User user = userService.findByEmail(email);
            // 기존 사용자라면 로그인 처리
            processOAuth2Login(request, response, user);
        } catch (IllegalArgumentException e) {
            // 신규 사용자라면 회원가입 페이지로 리다이렉트
            String name = (String) oAuth2User.getAttributes().get("name");
            redirectToRegister(request, response, email, name);
        }
    }

    private void processOAuth2Login(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        String jwtToken = tokenProvider.generateToken(user, JWT_TOKEN_DURATION);
        saveJwtToken(user.getId(), jwtToken);
        addJwtTokenToCookie(request, response, jwtToken);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void redirectToRegister(HttpServletRequest request, HttpServletResponse response, String email, String name) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(REGISTER_PATH)
                .queryParam("email", email)
                .queryParam("name", name)
                .build()
                .toUriString();

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveJwtToken(Long userId, String newJwtToken) {
        JwtToken jwtToken = jwtTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newJwtToken))
                .orElse(new JwtToken(userId, newJwtToken));

        jwtTokenRepository.save(jwtToken);
    }

    private void addJwtTokenToCookie(HttpServletRequest request, HttpServletResponse response, String jwtToken) {
        int cookieMaxAge = (int) JWT_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, JWT_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, JWT_TOKEN_COOKIE_NAME, jwtToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}