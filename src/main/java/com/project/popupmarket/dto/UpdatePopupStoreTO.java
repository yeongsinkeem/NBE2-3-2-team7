package com.project.popupmarket.dto;


import com.project.popupmarket.entity.PopupStoreImageList;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdatePopupStoreTO {
    private int updateStatus; // 업데이트 성공 여부
    private List<PopupStoreImageList> updatedImages;
}
