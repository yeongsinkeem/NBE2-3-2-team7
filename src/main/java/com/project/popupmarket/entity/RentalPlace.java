package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "RentalPlace")
public class RentalPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    @Column(nullable = false)
    private String email; // 이메일

    @Column(nullable = false)
    private String thumbnail; // 대표 이미지

    @Column(nullable = false)
    private int area; // 면적

    @Column(nullable = false)
    private long price; // 가격

    @Column(nullable = false)
    private String zipcode; // 우편번호

    @Column(nullable = false)
    private String address; // 주소

    private String addressDetail; // 상세주소

    private String description; // 임대지 설명

    @Column(nullable = false)
    private String name; // 임대지 이름

    @Column(nullable = false)
    private int capacity; // 수용인원

    @Column(nullable = false)
    private String infra; // 인프라

    private AgeGroup age; // 주변 연령대 확인 (ENUM?)

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now(); // 등록일자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus state = RentalStatus.available; // 임대 상태

    public enum AgeGroup {
        teans, twenties, thirties,
    }

    public enum RentalStatus {
        available,
        notavailable
    }
}
