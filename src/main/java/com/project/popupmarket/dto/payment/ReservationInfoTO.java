package com.project.popupmarket.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReservationInfoTO {
    private String customerKey;
    private String userName;
    private String userEmail;
    private String userTel;
    private String placeName;
    private String area;
    private String address;
    private String addrDetail;
    private BigDecimal price;
}
