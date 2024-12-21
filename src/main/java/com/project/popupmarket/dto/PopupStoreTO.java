package com.project.popupmarket.dto;

import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PopupStoreTO {
    private long seq;
    // private User popup_user_seq;
    private String thumbnail;
    private String type;
    private String targetAgeGroup;
    private String title;
    private String description;
    private String targetLocation;
    private Integer wishArea;
    private LocalDate startDate;
    private LocalDate endDate;

    public PopupStoreTO(long seq, String thumbnail, String type, String targetAgeGroup, String title, String description, String targetLocation, int wishArea, LocalDate startDate, LocalDate endDate) {
        this.seq = seq;
        // this.popup_user_seq = popup_user_seq;
        this.thumbnail = thumbnail;
        this.type = type;
        this.targetAgeGroup = targetAgeGroup;
        this.title = title;
        this.description = description;
        this.targetLocation = targetLocation;
        this.wishArea = wishArea;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PopupStoreTO(PopupStore popup) {
    }
}
