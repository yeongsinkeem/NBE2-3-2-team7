package com.project.popupmarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@ToString
public class RentalPlaceImageListId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1610323546044463757L;
    @NotNull
    @Column(name = "rental_place_seq", nullable = false)
    private Long rentalPlaceSeq;

    @Size(max = 255)
    @NotNull
    @Column(name = "image", nullable = false)
    private String image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RentalPlaceImageListId entity = (RentalPlaceImageListId) o;
        return Objects.equals(this.image, entity.image) &&
                Objects.equals(this.rentalPlaceSeq, entity.rentalPlaceSeq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, rentalPlaceSeq);
    }

}