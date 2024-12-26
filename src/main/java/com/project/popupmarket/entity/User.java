package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name", unique = true)
    private String name;

//    @Column(name = "social")
//    private String social;

    @Column(name = "brand")
    private String brand;

    @Column(name = "tel")
    private String tel;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @ElementCollection
    @CollectionTable(name = "user_attributes", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;

    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String password, String name, String social, String brand, String tel, String profileImage, Map<String, String> attributes) {
        this.email = email;
        this.password = password;
        this.name = name;
//        this.social = social;
        this.brand = brand;
        this.tel = tel;
        this.profileImage = profileImage;
        this.attributes = attributes;
    }

    public User update(String nickname) {
        this.name = nickname;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}