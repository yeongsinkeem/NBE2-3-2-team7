package com.project.popupmarket.dto.rentalDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RentalPlaceImageTO {
    private Long rentalPlaceSeq;
    private String image;

    public RentalPlaceImageTO(Long rentalPlaceSeq, String image) {
        this.rentalPlaceSeq = rentalPlaceSeq;
        this.image = image;
    }
}
