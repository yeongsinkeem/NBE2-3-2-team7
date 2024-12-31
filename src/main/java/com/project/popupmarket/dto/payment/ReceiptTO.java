package com.project.popupmarket.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptTO {
    private String paymentKey;
    private String orderId;
    private Long popupUserSeq;
    private Long rentalPlaceSeq;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("amount")
    private BigDecimal totalAmount;
}
