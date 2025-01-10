package com.project.popupmarket.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentTO {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
