package com.project.popupmarket.service;

import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
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


    // 2 - 1. Read : 조건에 해당하는 팝업 미리보기
    public List<PopupStoreTO> findByFilter(String targetLocation, String type, String targetAgeGroup, LocalDate startDate, LocalDate endDate) {
        List<PopupStore> popupStores = popupStoreJpaRepository.findByFilter(targetLocation, type, targetAgeGroup, startDate, endDate);

        ModelMapper modelMapper = new ModelMapper();
        List<PopupStoreTO> lists = popupStores.stream()
                .map(p -> modelMapper.map(p, PopupStoreTO.class))
                .collect(Collectors.toList());

        return lists;
    }

    // 2 - 2. Read : 특정 번호에 해당하는 팝업스토어 상세 정보
    public PopupStoreTO findBySeq(Long seq) {
        PopupStore popupStore = popupStoreJpaRepository.findById(Long.valueOf(seq)).orElse(null);

        if (popupStore == null) {
            return null;
        }

        ModelMapper modelMapper = new ModelMapper();
        PopupStoreTO to = modelMapper.map(popupStore, PopupStoreTO.class);

        return to;
    }
    /*
    public PopupStoreTO findPopupStoreBySeq(Long id) {
        PopupStore popupStore = popupStoreJpaRepository.findPopupStoreBySeq(String.valueOf(id)).orElse(null);

        ModelMapper modelMapper = new ModelMapper();
        PopupStoreTO to = modelMapper.map(popupStore, PopupStoreTO.class);
        return to;
    }
     */

    public boolean update(PopupStoreTO to) {
        // 엔티티 조회
        PopupStore popupStore = popupStoreJpaRepository.findById(to.getSeq())
                .orElseThrow(() -> new RuntimeException("PopupStore not found with id: " + to.getSeq()));

        // 필드 업데이트 (null 값 확인)
        if (to.getTitle() != null) {
            popupStore.setTitle(to.getTitle());
        }
        if (to.getDescription() != null) {
            popupStore.setDescription(to.getDescription());
        }
        if (to.getStartDate() != null) {
            popupStore.setStartDate(to.getStartDate());
        }
        if (to.getEndDate() != null) {
            popupStore.setEndDate(to.getEndDate());
        }

        // 엔티티 저장
        popupStoreJpaRepository.save(popupStore);

        // 성공 여부 반환
        return true;
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
