package com.project.popupmarket.dto;

import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PopupStoreTO {
    private long seq;
    // private User popup_user_seq;
    private String title;
    private String type;
    private String thumbnail;
    private String targetAgeGroup;
    private String targetLocation;
    private Integer wishArea;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public PopupStoreTO(String title, String type, String targetAgeGroup, String targetLocation, Integer wishArea, String description, LocalDate startDate, LocalDate endDate) {
        // this.seq = seq;
        this.title = title;
        this.type = type;
        // this.thumbnail = thumbnail;
        this.targetAgeGroup = targetAgeGroup;
        this.targetLocation = targetLocation;
        this.wishArea = wishArea;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
