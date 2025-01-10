package com.project.popupmarket.dto.payment;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfoResponse {
    private String customerKey;
    private String userName;
    private String userEmail;
    private String userTel;
    private String placeName;
    private String zipcode;
    private String address;
    private String addrDetail;
    private BigDecimal price;
}
