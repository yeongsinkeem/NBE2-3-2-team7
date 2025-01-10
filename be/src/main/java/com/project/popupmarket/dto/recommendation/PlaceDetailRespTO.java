package com.project.popupmarket.dto.recommendation;

import com.project.popupmarket.dto.payment.RangeDateTO;
import com.project.popupmarket.dto.land.RentalLandRespTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PlaceDetailRespTO {
    private List<RangeDateTO> reservationPeriod;
    private RentalLandRespTO data;
}
