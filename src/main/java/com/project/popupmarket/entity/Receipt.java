package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "receipts")
public class Receipt {
    @Id
    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "popup_user_seq", nullable = false)
    private Long popupUserSeq;

    @Column(name = "rental_place_seq", nullable = false)
    private Long rentalPlaceSeq;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_amount", precision = 10)
    private BigDecimal totalAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus;

    @NotNull
    @Column(name = "reserved_at", nullable = false, updatable = false)
    private LocalDateTime reservedAt = LocalDateTime.now();

    @Getter
    public enum ReservationStatus {
        COMPLETED("결제 완료"),
        LEASED("임대 완료"),
        CANCELED("환불 완료");

        private final String desc;

        ReservationStatus(String desc) {
            this.desc = desc;
        }
    }
}