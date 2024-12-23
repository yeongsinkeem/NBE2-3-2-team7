package com.project.popupmarket.controller;

import com.project.popupmarket.dto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.service.RentalPlaceServiceImpl;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PopupMarketController {
    @Autowired
    private RentalPlaceServiceImpl rentalPlaceService;

    @GetMapping("/main")
    public ResponseEntity<List<RentalPlaceTO>> MainPage() { // main 페이지 최신 등록 데이터 10개
        List<RentalPlaceTO> results = rentalPlaceService.findWithLimit();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/list")
    public ResponseEntity<Page<RentalPlaceTO>> ListPage( // 리스트 페이지 + 필터링 + 페이지네이션
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page
    ) {
        //Query Parameter -> DTO?
//        GET /list?page=0 -> 초기 값
//        GET /list?minArea=30&maxArea=70&location=서울&minPrice=100000&maxPrice=9000000&page=0

        minCapacity = (minCapacity != null) ? minCapacity : 0;
        maxCapacity = (maxCapacity != null) ? maxCapacity : 100;
        minPrice = (minPrice != null) ? minPrice : new BigDecimal(0);
        maxPrice = (maxPrice != null) ? maxPrice : new BigDecimal(10000000);

        Pageable pageable = PageRequest.of(page, 9);
        Page<RentalPlaceTO> results = rentalPlaceService.findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, pageable);
//        Page<RentalPlaceTO> results = rentalPlaceService.findFilteredWithPagination(30, 70, null, new BigDecimal(100000), new BigDecimal(9000000), pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/detail/{id}")
    public ResponseEntity<Map<String, Object>> RentalPlaceDetailsPage(
            @PathVariable Long id
    ) {
        RentalPlaceTO rentalPlace = rentalPlaceService.findDetailById(id);
        List<RentalPlaceImageListTO> imageTo = rentalPlaceService.findRentalPlaceImageList(id);



        Map<String, Object> response = new HashMap<>();
        response.put("rentalPlace", rentalPlace);
        response.put("images", imageTo);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/mypage/rental/view/{id}")//마이페이지 임대 리스트
    public ResponseEntity<List<RentalPlaceTO>> RentalListPage(
            @PathVariable("id") Long id
    ) {
        List<RentalPlaceTO> to = rentalPlaceService.findRentalPlacesByUserId(id);
        return ResponseEntity.ok(to);
    }

    @PostMapping("/mypage/rental/add")
    public ResponseEntity<Void> InsertRentalPlacePage(//임대페이지 인서트
            @RequestBody RentalPlaceTO to
    ) {
        rentalPlaceService.insertRentalPlace(to);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/myPage/rental/update/{id}")
    public ResponseEntity<Map<String, Object>> updateRentalPlacePages(
            @PathVariable Long id
    ) {
        RentalPlaceTO to = rentalPlaceService.findById(id);
        List<RentalPlaceImageListTO> imageTo = rentalPlaceService.findRentalPlaceImageList(id);

        Map<String, Object> response = new HashMap<>();
        response.put("rentalPlace", to);
        response.put("images", imageTo);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/myPage/rental/update_ok/{id}")  //임대페이지 데이터 업데이트
    public ResponseEntity<Void> updateRentalPlacePage_ok(
            @PathVariable("id") Long id,
            @RequestBody RentalPlaceTO to) {
        to.setId(id);
        rentalPlaceService.insertRentalPlace(to);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/myPage/rental/delete/{id}")
    public ResponseEntity<Void> deleteRentalPlacePage(
            @PathVariable Long id
    ) {
        rentalPlaceService.deleteRentalPlaceById(id);
        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }



}
