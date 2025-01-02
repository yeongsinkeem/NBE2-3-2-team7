package com.project.popupmarket.dto.rentalDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentalPlaceRespTO {
    private RentalPlaceTO rentalPlace;
    private List<RentalPlaceImageTO> images;
}
