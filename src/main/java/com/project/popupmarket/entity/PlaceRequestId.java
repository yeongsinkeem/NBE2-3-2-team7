package com.project.popupmarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class PlaceRequestId implements Serializable {
    @Serial
    private static final long serialVersionUID = -888044858189011363L;
    @NotNull
    @Column(name = "popup_store_seq", nullable = false)
    private Long popupStoreSeq;

    @NotNull
    @Column(name = "rental_place_seq", nullable = false)
    private Long rentalPlaceSeq;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PlaceRequestId entity = (PlaceRequestId) o;
        return Objects.equals(this.popupStoreSeq, entity.popupStoreSeq) &&
                Objects.equals(this.rentalPlaceSeq, entity.rentalPlaceSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(popupStoreSeq, rentalPlaceSeq);
    }

}