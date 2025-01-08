package com.project.popupmarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
// Embeddable : 내장될 클래스에 사용
@Embeddable
public class PopupStoreImageListId implements Serializable {
    @Serial
    private static final long serialVersionUID = -1657955911995991137L;

    @NotNull
    @Column(name = "popup_store_seq", nullable = false)
    private Long popupStoreSeq;

    @Size(max = 255)
    @NotNull
    @Column(name = "image", nullable = false)
    private String image;

    // 1. 기본 생성자
    public PopupStoreImageListId() {}

    // 2. 필드 기반 생성자
    public PopupStoreImageListId(Long popupStoreSeq, String image) {
        this.popupStoreSeq = popupStoreSeq;
        this.image = image;
    }

    @Override
    public String toString() {
        return "PopupStoreImageListId { " +
                "popupStoreSeq = " + popupStoreSeq +
                ", image = '" + image + '\'' +
                '}';
    }
    // 3 - 1. equals()
    // 팝업스토어 번호와 이미지 값이 같으면 두 객체 동일하다고 판단
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PopupStoreImageListId entity = (PopupStoreImageListId) o;
        return Objects.equals(this.image, entity.image) &&
                Objects.equals(this.popupStoreSeq, entity.popupStoreSeq);
    }

    // 3 - 2. hashCode()
    // 객체의 해시코드 생성
    @Override
    public int hashCode() {
        return Objects.hash(image, popupStoreSeq);
    }

}