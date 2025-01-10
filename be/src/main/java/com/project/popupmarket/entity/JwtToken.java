package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "jwt_token", nullable = false)
    private String jwtToken;

    public JwtToken(Long userId, String jwtToken) {
        this.userId = userId;
        this.jwtToken = jwtToken;
    }

    public JwtToken update(String newJwtToken) {
        this.jwtToken = newJwtToken;

        return this;
    }
}
