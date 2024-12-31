package com.project.popupmarket.dto.bundle;

import com.project.popupmarket.dto.popupDto.PopupStoreTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MainRentalAndPopupTO {
    private List<PopupStoreTO> popupStore;
    private List<RentalPlaceTO> rentalPlace;
}
