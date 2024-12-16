package com.project.popupmarket.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class PopupStore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    private String email;
    private String thumbnail;
    private String type; // ENUM();
    private int targetArea; // 희망 면적
    private String targetLocation;
    private String targetAge; // ENUM ?? 확인 필요
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
