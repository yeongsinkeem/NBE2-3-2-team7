package com.project.popupmarket.dto.land;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentalLandRespTO {
    private RentalLandTO rentalLand;
    private List<RentalLandImageTO> images;
}
