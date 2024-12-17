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
    @EmbeddedId
    private PopupStoreImageListId id;

    @MapsId("popupStoreSeq")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "popup_store_seq", nullable = false)
    private PopupStore popupStoreSeq;

}