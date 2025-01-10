package com.project.popupmarket.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptsTO {
    private String paymentKey;
    private String orderId;
    private Long customerId;
    private Long rentalLandId;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("amount")
    private BigDecimal totalAmount;
}
