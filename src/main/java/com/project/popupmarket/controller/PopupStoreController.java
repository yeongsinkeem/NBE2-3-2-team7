package com.project.popupmarket.controller;


import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import com.project.popupmarket.service.PopupStoreServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PopupStoreController {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;
    @Autowired
    private PopupStoreServiceImpl popupStoreServiceImpl;

    // 1. Create
    @PostMapping("/mypage/popup/add")
    @Operation(summary = "팝업스토어 추가")
    public ResponseEntity<String> createPopup(@RequestBody PopupStoreTO popupStore) {
        PopupStoreTO to = new PopupStoreTO();

        // long popupSeq = popupStore.getSeq();
        // to.setPopup_user_seq(popupStore.getPopup_user_seq());
        to.setThumbnail(popupStore.getThumbnail());
        to.setType(popupStore.getType());
        to.setTargetAgeGroup(popupStore.getTargetAgeGroup());
        to.setTitle(popupStore.getTitle());
        to.setDescription(popupStore.getDescription());
        to.setTargetLocation(popupStore.getTargetLocation());
        to.setStartDate(popupStore.getStartDate());
        to.setEndDate(popupStore.getEndDate());

        int flag = popupStoreServiceImpl.insert(to);

        if (flag == 1) {
            return new ResponseEntity<>("팝업스토어가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
        } else if (flag == 0) {
            return new ResponseEntity<>("팝업스토어 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2 - 1. Read : 조건에 해당하는 팝업들 미리보기 - targetLocation, type, targetAgeGroup, startDate ~ endDate
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

    // 2 - 2. Read : 번호에 해당하는 팝업
    // 특정 번호에 해당하는 팝업스토어 상세 정보
    @GetMapping("/popup/detail/{seq}")
    @Operation(summary = "개별 팝업 조회" )
    public PopupStoreTO getPopupBySeq(@PathVariable Long seq) {
        PopupStoreTO to = popupStoreServiceImpl.findBySeq(seq);

        if ( to == null ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PopupStore not found with seq: " + seq);
        }

        return to;
    }

    // 2 - 3. Read : 관리 중인 팝업 페이지 목록
    // thumbnail, seq(팝업 기획자의 팝업 데이터), type, title, 입점 요청 몇 회 받았는지
    // @GetMapping("/mypage/popup")

    // 3. Update : 번호에 해당하는 팝업 수정
    @PutMapping("/mypage/popup/edit/{seq}")
    @Operation(summary = "개별 팝업 수정")
    public ResponseEntity<String> updatePopup(@PathVariable Long seq, @RequestBody PopupStoreTO popupStore) {
        int result = popupStoreServiceImpl.update(seq, popupStore);

        if (result > 0) {
            return ResponseEntity.ok("PopupStore updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PopupStore not found");
        }
    }

    // 4. Delete
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
