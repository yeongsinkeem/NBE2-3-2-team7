package com.project.popupmarket.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReservationResponse {
    private String rentalLandTitle;
    private List<ReceiptsInfoTO> reservation;
}
