package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "popupStoreImageList")
public class PopupStoreImageList {
    // 복합키 사용
    @EmbeddedId
    private PopupStoreImageListId id;

    @MapsId("popupStoreSeq") // 외래 키. 팝업스토어 테이블과 연결
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "popup_store_seq", nullable = false)
    private PopupStore popupStoreSeq;

}