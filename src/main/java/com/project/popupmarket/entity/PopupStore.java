package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "PopupStore")
public class PopupStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_user_seq")
    private User popupUserSeq;

    @Size(max = 255)
    @Column(name = "thumbnail")
    private String thumbnail;

    @NotNull
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Size(max = 255)
    @Column(name = "target_age_group")
    private String targetAgeGroup;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Size(max = 255)
    @Column(name = "target_location")
    private String targetLocation;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @OneToMany(mappedBy = "popupStoreSeq")
    private Set<PlaceRequest> rentalPlaces = new LinkedHashSet<>();

    @OneToMany(mappedBy = "popupStoreSeq")
    private Set<PopupStoreImageList> popupStoreImageLists = new LinkedHashSet<>();

}