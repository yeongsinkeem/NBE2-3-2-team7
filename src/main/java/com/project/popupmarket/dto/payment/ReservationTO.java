package com.project.popupmarket.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReservationTO {
    private Long userSeq;
    private Long rentalPlaceSeq;
    private LocalDate startDate;
    private LocalDate endDate;
}
