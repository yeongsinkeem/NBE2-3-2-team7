package com.project.popupmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "rentalPlaceImageList")
public class RentalPlaceImageList {
    @EmbeddedId
    private RentalPlaceImageListId id;

    @MapsId("rentalPlaceSeq")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "rental_place_seq", nullable = false)
    private RentalPlace rentalPlaceSeq;

}