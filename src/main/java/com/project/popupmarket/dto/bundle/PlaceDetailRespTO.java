package com.project.popupmarket.dto.bundle;

import com.project.popupmarket.dto.payment.RangeDateTO;
import com.project.popupmarket.dto.popupDto.PopupStoreImgDTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceRespTO;
import com.project.popupmarket.dto.rentalDto.UserRentalPlaceInfoTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PlaceDetailRespTO {
    private List<RangeDateTO> reservationPeriod;
    private RentalPlaceRespTO data;
}
