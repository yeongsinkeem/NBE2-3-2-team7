package com.project.popupmarket.entity;

import com.project.popupmarket.constant.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    @Column(nullable = false)
    private String name;
    private String social;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    private String tel;
    @Column(nullable = false)
    private String brandImage;
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<RentalPlace> rentalPlaces = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Receipts> receipts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PopupStore> popupStores = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<JwtToken> tokens = new ArrayList<>();

    protected User() {
    }

    public User(String email, String password, String name, String brand, String tel, String social) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.brand = brand;
        this.tel = tel;
        this.social = social;
        this.brandImage = "brand_default.png"; // 기본값 설정
        this.registeredAt = LocalDateTime.now(); // 기본값 설정
        this.role = Role.USER;
    }
}
