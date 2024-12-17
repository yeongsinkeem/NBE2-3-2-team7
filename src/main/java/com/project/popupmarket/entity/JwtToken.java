package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "jwtToken")
public class JwtToken {
    @EmbeddedId
    private JwtTokenId id;

    @MapsId("userSeq")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_seq", nullable = false)
    private User userSeq;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

}