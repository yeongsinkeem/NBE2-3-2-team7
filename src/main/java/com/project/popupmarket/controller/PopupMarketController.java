package com.project.popupmarket.controller;

import com.project.popupmarket.dto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.RentalPlaceRequestTO;
import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.service.RentalPlaceServiceImpl;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
//        Page<RentalPlaceTO> results = rentalPlaceService.findFilteredWithPagination(30, 70, null, new BigDecimal(100000), new BigDecimal(9000000), pageable);

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/detail/{id}")
    public ResponseEntity<Map<String, Object>> RentalPlaceDetailsPage(
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

    @PostMapping(value = "/mypage/rental/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PostMapping("/mypage/rental/add")//임대페이지 인서트
    public ResponseEntity<Void> InsertRentalPlacePage(
//            @RequestBody RentalPlaceRequestTO requestTO
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlaceTO,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {
//        RentalPlaceTO rentalPlaceTO = requestTO.getRentalPlace();
//        MultipartFile thumbnail = requestTO.getThumbnail();
//        List<MultipartFile> images = requestTO.getImages();

        System.out.println("Rental Place: " + rentalPlaceTO);
        System.out.println("Thumbnail: " + thumbnail.getOriginalFilename());
        images.forEach(image -> System.out.println("Image: " + image.getOriginalFilename()));

        rentalPlaceService.insertRentalPlaceWithImages(rentalPlaceTO, thumbnail, images);

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
//            @RequestBody RentalPlaceRequestTO requestTO
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlaceTO,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {
//        RentalPlaceTO rentalPlaceTO = requestTO.getRentalPlace();
//        MultipartFile thumbnail = requestTO.getThumbnail();
//        List<MultipartFile> images = requestTO.getImages();

        rentalPlaceTO.setId(id);

        rentalPlaceService.insertRentalPlace(rentalPlaceTO, thumbnail);
        rentalPlaceService.deleteRentalPlaceImageById(id);
        rentalPlaceService.updateRentalPlaceImage(id,images);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/myPage/rental/delete/{id}")
    public ResponseEntity<Void> deleteRentalPlacePage(
            @PathVariable Long id
    ) {
        rentalPlaceService.deleteRentalPlaceImageById(id);
        rentalPlaceService.deleteRentalPlaceById(id);

        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }

//    썸네일 : rental_임대번호(seq)_회원번호(seq)_thumbnail.확장자
//    이미지 리스트 : rental_임대번호(seq)_회원번호(seq)_images_(인덱스).확장자
//===========================================

//    @GetMapping("/mypage/rental/addtest")//임대페이지 인서트
//    public ResponseEntity<Void> InsertRentalPlaceTestPage(
//    ) throws IOException {
//        RentalPlaceTO rentalPlace = new RentalPlaceTO();
//        rentalPlace.setRentalUserSeqId(1L); // 사용자 ID
////        rentalPlace.setThumbnail("thumbnail_default.png");
//
//        rentalPlace.setZipcode("12345");
//        rentalPlace.setPrice(new BigDecimal("500000"));
//        rentalPlace.setAddress("서울시 강남구");
//        rentalPlace.setAddrDetail("상세주소");
//        rentalPlace.setDescription("테스트 설명");
//        rentalPlace.setInfra("Wi-Fi, 주차장");
//        rentalPlace.setName("테스트 장소");
//        rentalPlace.setCapacity("50");
//        rentalPlace.setNearbyAgeGroup("20, 30");
//        rentalPlace.setRegisteredAt(Instant.now());
//        rentalPlace.setStatus("ACTIVE");
//
//        List<MultipartFile> images = new ArrayList<>();
//        MultipartFile thumbnail = null;
//        rentalPlace.setThumbnail(null);
//
//        rentalPlaceService.insertRentalPlaceWithImages(rentalPlace, thumbnail, images);
//
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @GetMapping("/myPage/rental/update_oktest/{id}")  //임대페이지 데이터 업데이트
//    public ResponseEntity<Void> updateRentalPlaceTestPage_ok(
//    ) {
//        Long id = 23L;
//
//        RentalPlaceTO rentalPlace = new RentalPlaceTO();
//        rentalPlace.setRentalUserSeqId(1L); // 사용자 ID
//        rentalPlace.setThumbnail("thumbnail_default.png");
//        rentalPlace.setZipcode("12345");
//        rentalPlace.setPrice(new BigDecimal("500000"));
//        rentalPlace.setAddress("서울시 강남구");
//        rentalPlace.setAddrDetail("상세주소");
//        rentalPlace.setDescription("테스트 설명");
//        rentalPlace.setInfra("Wi-Fi, 주차장");
//        rentalPlace.setName("테스트 장소");
//        rentalPlace.setCapacity("50");
//        rentalPlace.setNearbyAgeGroup("20, 30");
//        rentalPlace.setRegisteredAt(Instant.now());
//        rentalPlace.setStatus("ACTIVE");
//
//// 이미지 리스트 생성
//        List<RentalPlaceImageListTO> imageList = new ArrayList<>();
//        imageList.add(new RentalPlaceImageListTO(null, "image11.png"));
//        imageList.add(new RentalPlaceImageListTO(null, "image22.png"));
//        imageList.add(new RentalPlaceImageListTO(null, "image33.png"));
//
//        rentalPlace.setId(id);
//        rentalPlaceService.insertRentalPlace(rentalPlace);
//        rentalPlaceService.deleteRentalPlaceImageById(id);
//        rentalPlaceService.updateRentalPlaceImage(id,imageList);
//
//        return ResponseEntity.noContent().build();
//    }




}
