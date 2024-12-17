package com.project.popupmarket.controller;


import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import com.project.popupmarket.service.PopupStoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class PopupStoreController {

    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;

    @Autowired
    private PopupStoreServiceImpl popupStoreServiceImpl;

    @RequestMapping("/insert")
    public String insert() {
        PopupStoreTO popupStore1 = new PopupStoreTO();
        popupStore1.setSeq(1L);
        // popupStore1.setPopupUserSeq(1001);
        popupStore1.setThumbnail("/images/thumbnail.png");
        popupStore1.setType("애니메이션");
        popupStore1.setTargetAgeGroup("10대~20대");
        popupStore1.setTitle("체인소맨 팝업");
        popupStore1.setDescription("체인소맨 애니메이션 제작 3주년을 기념하여 팝퍼블에서 팝업을 오픈합니다.");
        popupStore1.setTargetLocation("서울");
        popupStore1.setStartDate(LocalDate.of(2024, 3, 1)); // LocalDate 사용
        popupStore1.setEndDate(LocalDate.of(2024, 5, 1));   // LocalDate 사용

        popupStoreServiceImpl.insert(popupStore1);
        return "<h3>insert</h3>";

    }
    // 모든 팝업의 상세 정보
    @RequestMapping("/selectAll")
    public String selectAll() {
        List<PopupStoreTO> results = popupStoreServiceImpl.findAll();

        for(PopupStoreTO to : results) {
            System.out.println(to);
            System.out.println(to.getTitle());
        }
        return "<h3>selectAll</h3>";
    }

}
