package com.project.popupmarket.dto.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptsInfoTO {
    private String orderId;
    private String reservationStatus;
    private String rentalPlaceName;
    private String reservationUserName;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
