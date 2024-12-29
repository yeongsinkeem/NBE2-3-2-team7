package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "popupStoreImageList")
public class PopupStoreImageList {
    // 복합키 사용
    @EmbeddedId
    private PopupStoreImageListId id;  // 복합키를 사용하여 popupStoreSeq와 image를 관리

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "popup_store_seq", insertable = false, updatable = false) // 복합키에서 관리되므로 insertable, updatable은 false로
    private PopupStore popupStoreSeq;  // 외래키 설정은 복합키를 통해 관리

    public PopupStoreImageList(PopupStoreImageListId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PopupStoreImageList{" +
                "id = " + id +
                '}';
    }
}
