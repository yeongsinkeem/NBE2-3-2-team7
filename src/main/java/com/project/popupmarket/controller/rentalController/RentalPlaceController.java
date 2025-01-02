package com.project.popupmarket.controller.rentalController;

import com.project.popupmarket.dto.rentalDto.RentalPlaceImageTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceRespTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import com.project.popupmarket.dto.rentalDto.UserRentalPlaceInfoTO;
import com.project.popupmarket.service.rentalService.RentalPlaceServiceImpl;
import com.project.popupmarket.util.UserContextUtil;
import io.swagger.v3.oas.annotations.Operation;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class RentalPlaceController {
    @Autowired
    private RentalPlaceServiceImpl rentalPlaceService;
    @Autowired
    private UserContextUtil userContextUtil;

    @GetMapping("/rental/list")
    @Operation(summary = "조건에 해당하는 임대지 리스트")
    public Page<RentalPlaceTO> rentalListPagination( // 임대 리스트 페이지 9개 + 필터링 + 페이지네이션
            @RequestParam(required = false) Integer minCapacity, // 최소 면적 기본값 0
            @RequestParam(required = false) Integer maxCapacity, // 최소 면적 기본값 100
            @RequestParam(required = false) String location,     // 위치, 기본값 null
            @RequestParam(required = false) BigDecimal minPrice, // 최소 가격 기본값 0
            @RequestParam(required = false) BigDecimal maxPrice, // 최소 가격 기본값 10000000
            @RequestParam(required = false) LocalDate startDate, // 시작일
            @RequestParam(required = false) LocalDate endDate,   // 종료일
            @RequestParam(required = false) String sorting,      // 정렬 기준
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

        return rentalPlaceService.findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, startDate, endDate, sorting, pageable);
    }

    @GetMapping("/rental/user")
    @Operation(summary = "사용자 임대지 리스트")
    public List<RentalPlaceTO> userRentalList() {
        Long userSeq = userContextUtil.getUserId();

        return rentalPlaceService.findRentalPlacesByUserId(userSeq);
    }

    @PatchMapping("/rental/{id}")
    @Operation(summary = "임대지 상태 변경 -> [ACTIVE, INACTIVE]")
    public ResponseEntity<Void> updateRentalStatus(
            @PathVariable("id") Long id,
            @RequestBody String status
    ) {
        rentalPlaceService.updateRentalPlaceStatus(id, status);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "임대지 추가")
    @PostMapping(value = "/rental", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> insertRentalPlace( // 임대페이지 데이터 create
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlace,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {
        Long userSeq = userContextUtil.getUserId();

        rentalPlace.setRentalUserSeqId(userSeq);

        rentalPlaceService.insertRentalPlaceWithImages(rentalPlace, thumbnail, images);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/rental/{id}")
    @Operation(summary = "개별 임대지 조회")
    public ResponseEntity<RentalPlaceRespTO> rentalPlaceById(
            @PathVariable Long id
    ) {
        RentalPlaceTO to = rentalPlaceService.findById(id);
        List<RentalPlaceImageTO> imageTo = rentalPlaceService.findRentalPlaceImageList(id);

        if (to == null) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(new RentalPlaceRespTO(to, imageTo));
    }

    @PutMapping("/rental/{id}")
    @Operation(summary = "개별 임대지 수정")
    public ResponseEntity<Void> updateRentalPlace(
            @PathVariable("id") Long id,
            @RequestPart("rentalPlace") RentalPlaceTO rentalPlaceTO,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart("images") List<MultipartFile> images
    ) {

        rentalPlaceTO.setId(id);
        rentalPlaceTO.setRentalUserSeqId(userContextUtil.getUserId());

        rentalPlaceService.insertRentalPlace(rentalPlaceTO, thumbnail);
        rentalPlaceService.deleteRentalPlaceImageById(id);
        rentalPlaceService.updateRentalPlaceImage(id,images);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rental/{id}")
    @Operation(summary = "개별 임대지 삭제")
    public ResponseEntity<Void> deleteRentalPlace(
            @PathVariable Long id
    ) {
        rentalPlaceService.deleteRentalPlaceById(id);

        return ResponseEntity.noContent().build(); // 상태 코드 204 반환
    }


}
