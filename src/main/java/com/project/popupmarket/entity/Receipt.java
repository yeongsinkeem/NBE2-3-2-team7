package com.project.popupmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "receipts")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_user_seq")
    private User popupUserSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_place_seq")
    private RentalPlace rentalPlaceSeq;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_amount", precision = 10)
    private BigDecimal totalAmount;

    @NotNull
    @Lob
    @Column(name = "reservation_status", nullable = false)
    private String reservationStatus;

    @NotNull
    @ColumnDefault("'active'")
    @Lob
    @Column(name = "status", nullable = false)
    private String status;

}