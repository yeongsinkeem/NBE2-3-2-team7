package com.project.popupmarket.util;

import com.project.popupmarket.config.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextUtil {

    private final TokenProvider tokenProvider;

    @Autowired
    public UserContextUtil(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String token = (String) authentication.getCredentials();
            return tokenProvider.getUserId(token);
        }
        return null;
    }
}
