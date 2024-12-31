package com.project.popupmarket.dto.payment;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RangeDateTO {
    private LocalDate startDate;
    private LocalDate endDate;
}
