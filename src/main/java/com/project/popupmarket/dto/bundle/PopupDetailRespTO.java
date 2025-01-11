package com.project.popupmarket.dto.bundle;

import com.project.popupmarket.dto.popupDto.PopupStoreImgDTO;
import com.project.popupmarket.dto.rentalDto.UserRentalPlaceInfoTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PopupDetailRespTO {
    private List<UserRentalPlaceInfoTO> userRentalPlace;
    private PopupStoreImgDTO data;
}
