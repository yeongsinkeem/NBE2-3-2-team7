package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "social")
    private String social;

    @Size(max = 255)
    @Column(name = "brand")
    private String brand;

    @Size(max = 20)
    @Column(name = "tel", length = 20)
    private String tel;

    @Size(max = 255)
    @Column(name = "profile_image")
    private String profileImage;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @OneToMany(mappedBy = "userSeq")
    private Set<JwtToken> jwtTokens = new LinkedHashSet<>();

    @OneToMany(mappedBy = "popupUserSeq")
    private Set<PopupStore> popupStores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rentalUserSeq")
    private Set<RentalPlace> rentalPlaces = new LinkedHashSet<>();

}