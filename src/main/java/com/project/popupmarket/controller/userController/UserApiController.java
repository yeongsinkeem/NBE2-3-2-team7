package com.project.popupmarket.controller.userController;

import com.project.popupmarket.config.handler.BaseAuthenticationSuccessHandler;
import com.project.popupmarket.config.jwt.TokenProvider;
import com.project.popupmarket.dto.userDto.UserRegisterDto;
import com.project.popupmarket.entity.JwtToken;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.JwtTokenRepository;
import com.project.popupmarket.service.userService.UserService;
import com.project.popupmarket.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserApiController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final JwtTokenRepository jwtTokenRepository;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserRegisterDto request,
                                                      HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // 회원 저장 및 ID 반환
        Long userId = userService.save(request);

        // 저장된 회원 정보 조회
        User user = userService.findById(userId);

        // JWT 토큰 생성
        String refreshToken = tokenProvider.generateToken(user, BaseAuthenticationSuccessHandler.JWT_TOKEN_DURATION);
        String accessToken = tokenProvider.generateToken(user, BaseAuthenticationSuccessHandler.ACCESS_TOKEN_DURATION);

        // 리프레시 토큰 저장
        jwtTokenRepository.save(new JwtToken(userId, refreshToken));

        // 쿠키에 리프레시 토큰 저장
        CookieUtil.addCookie(httpResponse,
                BaseAuthenticationSuccessHandler.JWT_TOKEN_COOKIE_NAME,
                refreshToken,
                (int) BaseAuthenticationSuccessHandler.JWT_TOKEN_DURATION.toSeconds());

        // 시큐리티 컨텍스트에 인증 정보 설정
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 리다이렉션 URL과 토큰을 Map으로 반환
        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", "/main");
        response.put("token", accessToken);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String bearerToken,
            HttpServletRequest request,
            HttpServletResponse response) {
        try {
            String token = bearerToken.replace("Bearer ", "");

            if (tokenProvider.validToken(token)) {
                Long userId = tokenProvider.getUserId(token);

                // DB에서 JWT 토큰 삭제
                jwtTokenRepository.findByUserId(userId)
                        .ifPresent(jwtTokenRepository::delete);

                // 쿠키 삭제
                CookieUtil.deleteCookie(request, response,
                        BaseAuthenticationSuccessHandler.JWT_TOKEN_COOKIE_NAME);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }}
