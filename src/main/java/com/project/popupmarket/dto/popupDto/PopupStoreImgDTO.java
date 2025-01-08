package com.project.popupmarket.dto.popupDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PopupStoreImgDTO {
    private PopupStoreTO popupStore;
    private List<String> images;

    public PopupStoreImgDTO(PopupStoreTO popupStore, List<String> images) {
        this.popupStore = popupStore;
        this.images = images;
    }
}
