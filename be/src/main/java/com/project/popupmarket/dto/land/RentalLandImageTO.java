package com.project.popupmarket.dto.land;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RentalLandImageTO {
    private Long rentalLandId;
    private String image;

    public RentalLandImageTO(Long rentalLandId, String image) {
        this.rentalLandId = rentalLandId;
        this.image = image;
    }
}
