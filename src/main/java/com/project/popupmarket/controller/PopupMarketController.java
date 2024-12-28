package com.project.popupmarket.controller;

import com.project.popupmarket.dto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.service.RentalPlaceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
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
        //name, price, address, thumbnail -> default
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
        //Capacity, Price, Name, Thumbnail

        minCapacity = (minCapacity != null) ? minCapacity : 0;
        maxCapacity = (maxCapacity != null) ? maxCapacity : 100;
        minPrice = (minPrice != null) ? minPrice : new BigDecimal(0);
        maxPrice = (maxPrice != null) ? maxPrice : new BigDecimal(10000000);

        Pageable pageable = PageRequest.of(page, 9);
        Page<RentalPlaceTO> results = rentalPlaceService.findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/detail/{id}")
    public ResponseEntity<Map<String, Object>> RentalPlaceDetailsPage( // 임대페이지 상세 정보
            @PathVariable Long id
    ) {
        // 썸네일과 상태 제외 모두 전송
        RentalPlaceTO rentalPlaceTO = rentalPlaceService.findDetailById(id);
        List<RentalPlaceImageListTO> imageTo = rentalPlaceService.findRentalPlaceImageList(id);


        Map<String, Object> response = new HashMap<>();
        response.put("rentalPlace", rentalPlaceTO);
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

    @PutMapping("/mypage/rental/view/{id}/status")//마이페이지 임대 리스트에서 status 변환 -> ACTIVE, INACTIVE
    public ResponseEntity<Void> UpdateRentalStatusPage(
            @PathVariable("id") Long id,
            @RequestParam String status
    ) {
        rentalPlaceService.updateRentalPlaceStatus(id, status);

        return ResponseEntity.ok().build();
    }



    @PostMapping(value = "/mypage/rental/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> InsertRentalPlacePage( // 임대페이지 데이터 create
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlaceTO,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {

        System.out.println("Rental Place: " + rentalPlaceTO);
        System.out.println("Thumbnail: " + thumbnail.getOriginalFilename());
        images.forEach(image -> System.out.println("Image: " + image.getOriginalFilename()));

        rentalPlaceService.insertRentalPlaceWithImages(rentalPlaceTO, thumbnail, images);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/myPage/rental/update/{id}")
    public ResponseEntity<Map<String, Object>> updateRentalPlacePages( // 임대페이지 데이터 가져오기
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
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlaceTO,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {

        rentalPlaceTO.setId(id);

        rentalPlaceService.insertRentalPlace(rentalPlaceTO, thumbnail);
        rentalPlaceService.deleteRentalPlaceImageById(id);
        rentalPlaceService.updateRentalPlaceImage(id,images);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/myPage/rental/delete/{id}") //임대페이지 데이터 삭제
    public ResponseEntity<Void> deleteRentalPlacePage(
            @PathVariable Long id
    ) {
        rentalPlaceService.deleteRentalPlaceById(id);

        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }


}
