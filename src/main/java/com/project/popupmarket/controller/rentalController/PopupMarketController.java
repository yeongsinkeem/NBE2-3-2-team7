package com.project.popupmarket.controller.rentalController;

import com.project.popupmarket.dto.rentalDto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import com.project.popupmarket.service.rentalService.RentalPlaceServiceImpl;
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
    public ResponseEntity<List<RentalPlaceTO>> mainPage() { // main 페이지 최신 등록 데이터 10개 조회
        //name, price, address, thumbnail -> default
        List<RentalPlaceTO> results = rentalPlaceService.findWithLimit();

        if (results == null || results.isEmpty()) { // 결과가 비어 있는 경우 204 No Content
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/list")
    public ResponseEntity<Page<RentalPlaceTO>> listPage( // 임대 리스트 페이지 9개 + 필터링 + 페이지네이션
            @RequestParam(required = false) Integer minCapacity, // 최소 면적 기본값 0
            @RequestParam(required = false) Integer maxCapacity, // 최소 면적 기본값 100
            @RequestParam(required = false) String location,     // 위치, 기본값 null
            @RequestParam(required = false) BigDecimal minPrice, // 최소 가격 기본값 0
            @RequestParam(required = false) BigDecimal maxPrice, // 최소 가격 기본값 10000000
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


        if (results.isEmpty()) { // 결과가 비어 있는 경우 204 No Content
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/rental/detail/{id}")
    public ResponseEntity<Map<String, Object>> rentalPlaceDetailsPage( // 임대페이지 상세 정보 조회
            @PathVariable Long id
    ) {
        // 썸네일과 상태 제외 모두 전송
        RentalPlaceTO rentalPlaceTO = rentalPlaceService.findDetailById(id); // 임대 상세 정보 조회
        List<RentalPlaceImageListTO> imageTo = rentalPlaceService.findRentalPlaceImageList(id); // 이미지 리스트 조회

        if (rentalPlaceTO == null) { // 결과가 비어 있는 경우 204 No Content
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("rentalPlace", rentalPlaceTO);
        response.put("images", imageTo);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/mypage/rental/view/{id}")//마이페이지 임대 리스트
    public ResponseEntity<List<RentalPlaceTO>> rentalListPage(
            @PathVariable("id") Long id
    ) {
        List<RentalPlaceTO> to = rentalPlaceService.findRentalPlacesByUserId(id);

        if (to.isEmpty()) { // 결과가 비어 있는 경우 204 No Content
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(to);
    }

    @PutMapping("/mypage/rental/view/{id}/status")//마이페이지 임대 리스트에서 status 변환 -> ACTIVE, INACTIVE
    public ResponseEntity<Void> updateRentalStatusPage(
            @PathVariable("id") Long id,
            @RequestParam String status
    ) {
        rentalPlaceService.updateRentalPlaceStatus(id, status);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/mypage/rental/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> insertRentalPlacePage( // 임대페이지 데이터 create
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

        if (to == null) { // 결과가 비어 있는 경우 204 No Content
            return ResponseEntity.noContent().build();
        }

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
