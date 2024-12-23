package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "rentalPlace")
public class RentalPlace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_user_seq")
    private User rentalUserSeq;

    @Size(max = 255)
    @Column(name = "thumbnail")
    private String thumbnail;

    @Size(min = 5, max = 5)
    @Column(name = "zipcode", length = 5, nullable = false)
    private String zipcode;

    @Column(name = "price", precision = 10)
    private BigDecimal price;

    @Size(max = 255)
    @Column(name = "address")
    private String address;

    @Size(max = 255)
    @Column(name = "addr_detail")
    private String addrDetail;

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "infra")
    private String infra;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "capacity")
    private String capacity;

    @Size(max = 255)
    @Column(name = "nearby_age_group")
    private String nearbyAgeGroup;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

    @OneToMany(mappedBy = "rentalPlaceSeq", cascade = CascadeType.ALL)
    private Set<PlaceRequest> popupStores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rentalPlaceSeq")
    private Set<Receipt> receipts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "rentalPlaceSeq")
    private Set<RentalPlaceImageList> rentalPlaceImageLists = new LinkedHashSet<>();

}