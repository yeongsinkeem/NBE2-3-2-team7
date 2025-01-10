package com.project.popupmarket.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    COMPLETED("결제 완료"),
    LEASED("임대 완료"),
    CANCELED("환불 완료");

    private final String desc;
}