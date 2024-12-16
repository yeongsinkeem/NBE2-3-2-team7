package com.project.popupmarket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Receipts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    private long popupUserSeq;
    private long RentalPlaceSeq;
    private LocalDate startDate;
    private LocalDate endDate;
    private long price;
    private String state; // ENUM;
}
