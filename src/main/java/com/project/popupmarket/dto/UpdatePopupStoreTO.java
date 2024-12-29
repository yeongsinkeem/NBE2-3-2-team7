package com.project.popupmarket.dto;


import com.project.popupmarket.entity.PopupStoreImageList;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdatePopupStoreTO {
    private int updateStatus; // 업데이트된 이미지 개수
    private List<PopupStoreImageList> updatedImages; // 이미지 파일 경로 리스트


}
