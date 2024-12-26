package com.project.popupmarket.service.userService;

import com.project.popupmarket.entity.JwtToken;
import com.project.popupmarket.repository.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtTokenService {
    private final JwtTokenRepository jwtTokenRepository;

    public JwtToken findByJwtToken(String jwtToken) {
        return jwtTokenRepository.findByJwtToken(jwtToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
