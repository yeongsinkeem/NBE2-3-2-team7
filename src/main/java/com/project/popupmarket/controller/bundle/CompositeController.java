package com.project.popupmarket.controller.bundle;

import com.project.popupmarket.dto.bundle.MainRentalAndPopupTO;
import com.project.popupmarket.dto.bundle.PlaceDetailRespTO;
import com.project.popupmarket.dto.bundle.PopupDetailRespTO;
import com.project.popupmarket.dto.popupDto.PopupStoreImgDTO;
import com.project.popupmarket.dto.popupDto.PopupStoreTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceImageTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceRespTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import com.project.popupmarket.repository.PopupStoreImageJpaRepository;
import com.project.popupmarket.service.popupService.PopupStoreServiceImpl;
import com.project.popupmarket.service.receipts.PaymentService;
import com.project.popupmarket.service.rentalService.RentalPlaceServiceImpl;
import com.project.popupmarket.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CompositeController {

    private final RentalPlaceServiceImpl rentalPlaceService;
    private final PopupStoreServiceImpl popupStoreService;
    private final PopupStoreImageJpaRepository popupStoreImageJpaRepository;
    private final UserContextUtil userContextUtil;
    private final PaymentService paymentService;

    @Autowired
    public CompositeController(RentalPlaceServiceImpl rentalPlaceService, PopupStoreServiceImpl popupStoreService, PopupStoreImageJpaRepository popupStoreImageJpaRepository, UserContextUtil userContextUtil, PaymentService paymentService) {
        this.rentalPlaceService = rentalPlaceService;
        this.popupStoreService = popupStoreService;
        this.popupStoreImageJpaRepository = popupStoreImageJpaRepository;
        this.userContextUtil = userContextUtil;
        this.paymentService = paymentService;
    }

    @GetMapping("/main/new")
    @Operation(summary = "메인 페이지 최신 데이터 각각 10개 조회")
    public ResponseEntity<MainRentalAndPopupTO> getNewData() {
        return ResponseEntity.ok(new MainRentalAndPopupTO(popupStoreService.findByLimit(), rentalPlaceService.findWithLimit()));
    }

    @GetMapping("/popup/bundle/{popupSeq}")
    @Operation(summary = "개별 팝업 조회 (feat. 권유 목록)" )
    public ResponseEntity<PopupDetailRespTO> getPopupBySeqWithPlaceInfo(@PathVariable Long popupSeq) {
        Long userSeq = userContextUtil.getUserId();

        PopupStoreTO to = popupStoreService.findBySeq(popupSeq);
        List<String> imgLst = popupStoreImageJpaRepository.findById_PopupStoreSeq(popupSeq);

        if (to == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(new PopupDetailRespTO(rentalPlaceService.findUserRentalPlaceInfo(userSeq, popupSeq), new PopupStoreImgDTO(to, imgLst)));
    }

    @GetMapping("/rental/bundle/{placeSeq}")
    @Operation(summary = "개별 임대지 조회 (feat. 예약 날짜)")
    public ResponseEntity<PlaceDetailRespTO> getPlaceBySeqWithReservationPeriod(@PathVariable Long placeSeq) {
        RentalPlaceTO to = rentalPlaceService.findById(placeSeq);
        List<RentalPlaceImageTO> imageTo = rentalPlaceService.findRentalPlaceImageList(placeSeq);

        if (to == null) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(new PlaceDetailRespTO(paymentService.getRangeDates(placeSeq), new RentalPlaceRespTO(to, imageTo)));
    }
}
