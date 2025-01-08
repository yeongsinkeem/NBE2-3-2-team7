package com.project.popupmarket.controller.popupController;

import com.project.popupmarket.dto.popupDto.PopupStoreImgDTO;
import com.project.popupmarket.dto.popupDto.PopupStoreTO;
import com.project.popupmarket.repository.PopupStoreImageJpaRepository;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import com.project.popupmarket.service.popupService.PopupStoreFileStorageService;
import com.project.popupmarket.service.popupService.PopupStoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PopupStoreController {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;
    @Autowired
    private PopupStoreServiceImpl popupStoreServiceImpl;
    @Autowired
    private PopupStoreFileStorageService popupStoreFileStorageService;
    @Autowired
    private PopupStoreImageJpaRepository popupStoreImageJpaRepository;

    // [ CREATE ]
    @PostMapping(value = "/mypage/popup/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "팝업스토어 추가")
    public ResponseEntity<String> createPopup(
            @RequestPart("popupStore") PopupStoreTO popupStore, @RequestPart("thumbnail") MultipartFile thimg, @RequestPart("images") List<MultipartFile> images) {
        try {
            List<String> imgNames = new ArrayList<>();
            long popupCount = popupStoreJpaRepository.count();

            // 1. 썸네일 이미지 저장
            String thPath = "src/main/resources/static/images/popup_thumbnail";

            // 썸네일 파일 이름 지정
            String originalFilename = thimg.getOriginalFilename();
            String thPath2 = "images/popup_thumbnail/";
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String thumbNailFileName = "popup_" + (popupCount + 1) + "_thumbnail." + ext;

            popupStoreFileStorageService.storeFile(thimg, thPath, thumbNailFileName);

            // 2. 상세 이미지 저장
            String imagesPath = "src/main/resources/static/images/popup_detail";

            int i = 1;
            for (MultipartFile img : images) {
                // 파일 이름 지정
                String imgOriginalFilename = img.getOriginalFilename();
                String ext2 = imgOriginalFilename.substring(imgOriginalFilename.lastIndexOf(".") + 1);
                String imgFileName = "popup_" + (popupCount + 1) +  "_images_" + i + "." + ext2;

                // 파일 저장
                popupStoreFileStorageService.storeFile(img, imagesPath, imgFileName);
                imgNames.add(imgFileName);

                i ++;
            }
            // 3. 팝업, 썸네일, 상세 이미지 DB 삽입
            int flag = popupStoreServiceImpl.insert(popupStore, thPath2 + thumbNailFileName, imgNames);

            if (flag == 0) {
                return new ResponseEntity<>("팝업스토어가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("팝업스토어 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("파일 저장 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // [ READ ] - 1
    // : 조건에 해당하는 팝업들 미리보기 - targetLocation, type, targetAgeGroup, startDate ~ endDate
    @GetMapping("/popup/list")
    @Operation(summary = "조건에 해당하는 팝업 리스트")
    public List<PopupStoreTO> getPopupByFilter(
            @RequestParam(required = false) String targetLocation,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String targetAgeGroup,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate ) {
        return popupStoreServiceImpl.findByFilter(targetLocation, type, targetAgeGroup, startDate, endDate);
    }

    // [ READ ] - 2
    // 특정 번호에 해당하는 팝업스토어 상세 정보
    @GetMapping("/popup/detail/{seq}")
    @Operation(summary = "개별 팝업 조회" )
    public PopupStoreImgDTO getPopupBySeq(@PathVariable Long seq) {
        PopupStoreTO to = popupStoreServiceImpl.findBySeq(seq);
        List<String> imgLst = popupStoreImageJpaRepository.findById_PopupStoreSeq(seq);

        if (to == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PopupStore not found with seq: " + seq);
        }
        return new PopupStoreImgDTO(to, imgLst);
    }

    // [ Read ] - 3 : 관리 중인 팝업 페이지 목록
    // User 기능 추가시 구현 예정
    // thumbnail, seq(팝업 기획자의 팝업 데이터), type, title, 입점 요청 몇 회 받았는지
    // @GetMapping("/mypage/popup")


    // [ Update ] : 개별 팝업 스토어 수정
    @PutMapping(value = "/mypage/popup/edit/{seq}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "개별 팝업 수정")
    public ResponseEntity<Map<String, Object>> updatePopup(@PathVariable Long seq,
                                              @RequestPart(value = "popupStore", required = false) PopupStoreTO popupStore,
                                              @RequestPart(value = "thumbnail", required = false) MultipartFile thimg,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        try {
            // 1. 팝업스토어 업데이트
            int thCount = popupStoreServiceImpl.updatePopup(seq, popupStore, thimg); // updatePopup 메서드는 int 반환
            // 2. 팝업스토어 이미지 업데이트
            int imgsCount = popupStoreServiceImpl.updatePopupImgs(seq, images);

            // 3. 응답 생성
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "popupStore updated succesfully");
            System.out.println("팝업스토어 업데이트 : " + thCount);
            // imgsCount - 1: 정상
            System.out.println("팝업스토어 이미지 업데이트 : " + imgsCount);

            return ResponseEntity.ok(responseBody);
        } catch ( Exception e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "An error occurred while updating PopupStore", "error", e.getMessage()));
        }
    }

    // [ Delete ]
    // 팝업 관리 페이지 "/mypage/popup/view?번호={팝업번호}" -> 해당 팝업에 대한 상세 정보와 삭제하기 버튼 같이 출력
    // 이때 삭제하기 버튼을 누르면 해당 요청 시행
    @DeleteMapping("/mypage/popup/delete/{seq}")
    @Operation(summary = "팝업 삭제")
    public ResponseEntity<String> deletePopup(@PathVariable long seq) {
        int result = popupStoreServiceImpl.delete(seq);

        if (result == 1) {
            return ResponseEntity.ok("팝업이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("삭제할 팝업을 찾을 수 없습니다.");
        }
    }


}
