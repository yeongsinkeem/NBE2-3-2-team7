package com.project.popupmarket.entity;

import com.project.popupmarket.enums.ActivateStatus;
import com.project.popupmarket.enums.AgeGroup;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rental_land")
public class RentalLand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long id;

    @Column(name = "landlord_id")
    private Long landlordId;

    @Column(name = "price", precision = 10)
    private BigDecimal price;

    @Size(min = 5, max = 5)
    @Column(name = "zipcode", length = 5, nullable = false)
    private String zipcode;

    @Size(max = 255)
    @Column(name = "address", nullable = false)
    private String address;

    @Size(max = 255)
    @Column(name = "addr_detail")
    private String addrDetail;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(name = "infra")
    private String infra;

    @Column(name = "area")
    private Integer area;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivateStatus status;
}