package com.project.popupmarket.dto.invitation;

import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationInfoTO {
    private String popupTitle;
    private List<RentalPlaceTO> rentalPlaces;
}
