package com.project.popupmarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class JwtTokenId implements Serializable {
    @Serial
    private static final long serialVersionUID = -2597360151072932156L;
    @NotNull
    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    @Size(max = 500)
    @NotNull
    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        JwtTokenId entity = (JwtTokenId) o;
        return Objects.equals(this.userSeq, entity.userSeq) &&
                Objects.equals(this.token, entity.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userSeq, token);
    }

}