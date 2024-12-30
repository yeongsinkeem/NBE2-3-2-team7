package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private String wishArea;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registered", nullable = false, updatable = false)
    private LocalDateTime registered = LocalDateTime.now();

    // [ 외래키 관련 설정 ]
    // 1. 하나의 PopupStore는 여러 개의 PlaceRequest 가질 수 있음
    // PlaceRequest ) "popupStoreSeq"가 외래 키 역할.
    @OneToMany(mappedBy = "popupStoreSeq")
    private Set<PlaceRequest> rentalPlaces = new LinkedHashSet<>();

    // 2. 하나의 PopupStore는 여러 개의 PopupStoreImageList 가질 수 있음
    // PopupStoreImageList ) "popupStoreSeq"가 외래 키 역할
    // 팝업 스토어와 이미지들이 함께 저장되고 삭제되도록 설정
    @OneToMany(mappedBy = "popupStoreSeq", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PopupStoreImageList> popupStoreImageLists = new LinkedHashSet<>();

    // [ Image 추가 관련 메서드 : 양방향 연관 관계 메서드 ]
    // 1. 이미지 추가
    /*
    public void addImage(PopupStoreImageList image) {
        popupStoreImageLists.add(image);
        image.setPopupStoreSeq(this);
    }
    // 2. 이미지 제거
    public void removeImage(PopupStoreImageList image) {
        popupStoreImageLists.remove(image);
        image.setPopupStoreSeq(null);
    }

     */


}