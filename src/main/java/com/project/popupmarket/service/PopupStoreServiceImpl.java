package com.project.popupmarket.service;

import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PopupStoreServiceImpl {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;

    // 1. Create : 데이터 삽입
    public int insert(PopupStoreTO to) {
        // 받을 땐 TO로, 반환은 entity
        int flag = 0;

        try {
            // 1. TO를 엔티티티로 매핑
            ModelMapper modelMapper = new ModelMapper();
            PopupStore popupStore = modelMapper.map(to, PopupStore.class);

            // 2. DB 저장
            popupStoreJpaRepository.save(popupStore);

            // 3. 성공 시 flag 값은 1
            flag = 1;

        } catch (Exception e) {
            System.out.println("데이터 삽입 중 에러 발생 : " + e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }
    // 2 - 1. Read : 특정 번호에 해당하는 팝업스토어 상세 정보
    public PopupStoreTO findBySeq(Long seq) {
        // Repository에서 해당 데이터 찾기
        PopupStore popupStore = popupStoreJpaRepository.findById(Long.valueOf(seq)).orElse(null);

        if (popupStore == null) {
            return null;
        }

        // 엔티티 -> TO로 매핑
        ModelMapper modelMapper = new ModelMapper();
        PopupStoreTO to = modelMapper.map(popupStore, PopupStoreTO.class);

        return to;
    }

    // 2 - 2. Read : 조건에 해당하는 팝업 미리보기
    public List<PopupStoreTO> findByFilter(String targetLocation, String type, String targetAgeGroup, LocalDate startDate, LocalDate endDate) {
        List<PopupStore> popupStores = popupStoreJpaRepository.findByFilter(targetLocation, type, targetAgeGroup, startDate, endDate);

        ModelMapper modelMapper = new ModelMapper();
        // 엔티티 -> TO로 매핑
        List<PopupStoreTO> lists = popupStores.stream()
                .map(p -> modelMapper.map(p, PopupStoreTO.class))
                .collect(Collectors.toList());

        return lists;
    }

    @Transactional
    public int update(Long id, PopupStoreTO popupStore) {
        // 기존 값 가져오기
        PopupStore existingPopup = popupStoreJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Popup not found"));

        // 값 설정 : 이때 변경 값만을 반영, 값이 null이면 기존 값 유지
        String thumbnail = (popupStore.getThumbnail() != null) ? popupStore.getThumbnail() : existingPopup.getThumbnail();
        String type = (popupStore.getType() != null) ? popupStore.getType() : existingPopup.getType();
        String targetAgeGroup = (popupStore.getTargetAgeGroup() != null) ? popupStore.getTargetAgeGroup() : existingPopup.getTargetAgeGroup();
        String targetLocation = (popupStore.getTargetLocation() != null) ? popupStore.getTargetLocation() : existingPopup.getTargetLocation();
        String title = (popupStore.getTitle() != null) ? popupStore.getTitle() : existingPopup.getTitle();
        Integer wishArea = (popupStore.getWishArea() != null) ? popupStore.getWishArea() : existingPopup.getWishArea();
        String description = (popupStore.getDescription() != null) ? popupStore.getDescription() : existingPopup.getDescription();
        LocalDate startDate = (popupStore.getStartDate() != null) ? popupStore.getStartDate() : existingPopup.getStartDate();
        LocalDate endDate = (popupStore.getEndDate() != null) ? popupStore.getEndDate() : existingPopup.getEndDate();

        // 업데이트 쿼리 실행
        return popupStoreJpaRepository.updatePopupStore(
                id, thumbnail, type, targetAgeGroup, targetLocation, title, wishArea, description, startDate, endDate
        );
    }


    // 4. Delete : 팝업리스트 삭제
    public int delete(long seq) {
        try {
            // ID를 기반으로 팝업 스토어 삭제
            popupStoreJpaRepository.deleteById(seq);
            return 1; // 삭제 성공
        } catch (EmptyResultDataAccessException e) {
            // ID가 잘못되었을 경우
            System.err.println("존재하지 않는 팝업으로 인해 삭제 실패: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("알 수 없는 오류로 삭제 실패: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
