package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

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
    // auto_increment + PK
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // 팝업 기획자(임차인) - 팝업 : 다대일
    @JoinColumn(name = "popup_user_seq") // 외래키 컬럼
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

    // 팝업스토어 희망 면적 추가
    // Null 값 허용
    @Column(name = "wish_area")
    private Integer wishArea;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // [ 외래키 관련 설정 ]
    // 1. 하나의 PopupStore는 여러 개의 PlaceRequest 가질 수 있음
    // PlaceRequest ) "popupStoreSeq"가 외래 키 역할.
    @OneToMany(mappedBy = "popupStoreSeq")
    private Set<PlaceRequest> rentalPlaces = new LinkedHashSet<>();

    // 2. 하나의 PopupStore는 여러 개의 PopupStoreImageList 가질 수 있음
    // PopupStoreImageList ) "popupStoreSeq"가 외래 키 역할
    @OneToMany(mappedBy = "popupStoreSeq")
    private Set<PopupStoreImageList> popupStoreImageLists = new LinkedHashSet<>();

}