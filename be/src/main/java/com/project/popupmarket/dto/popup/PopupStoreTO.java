package com.project.popupmarket.dto.popup;

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
    private Long id;
    private Long customerId;
    private String title;
    private String type;
    private String thumbnail;
    private String ageGroup;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;

    public PopupStoreTO(String title, String type, String ageGroup, String description, LocalDate startDate, LocalDate endDate) {
        // this.seq = seq;
        this.title = title;
        this.type = type;
        // this.thumbnail = thumbnail;
        this.ageGroup = ageGroup;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
