package com.project.popupmarket.controller.bundle;

import com.project.popupmarket.dto.bundle.MainRentalAndPopupTO;
import com.project.popupmarket.service.popupService.PopupStoreServiceImpl;
import com.project.popupmarket.service.rentalService.RentalPlaceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CompositeController {

    private final RentalPlaceServiceImpl rentalPlaceService;
    private final PopupStoreServiceImpl popupStoreService;

    @Autowired
    public CompositeController(RentalPlaceServiceImpl rentalPlaceService, PopupStoreServiceImpl popupStoreService) {
        this.rentalPlaceService = rentalPlaceService;
        this.popupStoreService = popupStoreService;
    }

    @GetMapping("/main/new")
    @Operation(summary = "메인 페이지 최신 데이터 각각 10개 조회")
    public ResponseEntity<MainRentalAndPopupTO> getNewData() {
        return ResponseEntity.ok(new MainRentalAndPopupTO(popupStoreService.findByLimit(), rentalPlaceService.findWithLimit()));
    }
}
