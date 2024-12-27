package com.project.popupmarket.service;

import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.dto.UpdatePopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.entity.PopupStoreImageList;
import com.project.popupmarket.entity.PopupStoreImageListId;
import com.project.popupmarket.repository.PopupStoreImageJpaRepository;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopupStoreServiceImpl {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;
    @Autowired
    private PopupStoreImageJpaRepository popupStoreImageJpaRepository;

    @Transactional
    public int insert(PopupStoreTO to, String thimg, List<String> imgs) {
        int flag = 0;
        try {
            // 1. to -> 엔티티 매핑
            ModelMapper modelMapper = new ModelMapper();
            PopupStore popupStore = modelMapper.map(to, PopupStore.class);

            // 2. 팝업스토어 삽입
            popupStoreJpaRepository.save(popupStore);

            // 3. 팝업 TO 썸네일 파일 이름 설정
            // ex ) "images/popup_thumbnail/~"
            popupStore.setThumbnail(thimg);
            System.out.println("팝업 썸네일 이름은 : " + popupStore.getThumbnail());

            // 4. 각 이미지에 대해 PopupStoreImageList 엔티티 생성
            List<PopupStoreImageList> imageLists = imgs.stream()
                    .map(image -> new PopupStoreImageList(new PopupStoreImageListId(popupStore.getId(), "images/popup_detail/" + image)))
                    .collect(Collectors.toList());

            // 5. 상세 이미지 DB 삽입
            popupStoreImageJpaRepository.saveAll(imageLists);

        } catch ( Exception e) {
            flag = 1;
            System.out.println("Error : " + e.getMessage());
        }
        finally {
            return flag;
        }
    }

    @Transactional
    public void updateThumbnail(Long seq, String thimg) {
        PopupStore popupStore = popupStoreJpaRepository.findById(seq).orElseThrow(() -> new RuntimeException("Popup store not found"));
        popupStore.setThumbnail(thimg);
        popupStoreJpaRepository.save(popupStore);
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
        // String thumbnail = (popupStore.getThumbnail() != null) ? popupStore.getThumbnail() : existingPopup.getThumbnail();
        String type = (popupStore.getType() != null) ? popupStore.getType() : existingPopup.getType();
        String targetAgeGroup = (popupStore.getTargetAgeGroup() != null) ? popupStore.getTargetAgeGroup() : existingPopup.getTargetAgeGroup();
        String targetLocation = (popupStore.getTargetLocation() != null) ? popupStore.getTargetLocation() : existingPopup.getTargetLocation();
        String title = (popupStore.getTitle() != null) ? popupStore.getTitle() : existingPopup.getTitle();
        Integer wishArea = (popupStore.getWishArea() != null) ? popupStore.getWishArea() : existingPopup.getWishArea();
        String description = (popupStore.getDescription() != null) ? popupStore.getDescription() : existingPopup.getDescription();
        LocalDate startDate = (popupStore.getStartDate() != null) ? popupStore.getStartDate() : existingPopup.getStartDate();
        LocalDate endDate = (popupStore.getEndDate() != null) ? popupStore.getEndDate() : existingPopup.getEndDate();

        // List<String> imgs = (popupStore.g)

        // 업데이트 쿼리 실행
        return popupStoreJpaRepository.updatePopupStore(
                id, type, targetAgeGroup, targetLocation, title, wishArea, description, startDate, endDate
        );
    }

    /*
    @Transactional
    public UpdatePopupStoreTO update2(Long id, PopupStoreTO popupStore, List<String> imgs) {
        // 1. 기존 값 가져오기
        PopupStore existingPopup = popupStoreJpaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Popup not found"));
        List<String> existingImgs = popupStoreImageJpaRepository.findById_PopupStoreSeq(id);

        // 2. 팝업스토어 수정 : 이때 변경 값만을 반영, 값이 null이면 기존 값 유지
        // String thumbnail = (popupStore.getThumbnail() != null) ? popupStore.getThumbnail() : existingPopup.getThumbnail();
        String type = (popupStore.getType() != null) ? popupStore.getType() : existingPopup.getType();
        String targetAgeGroup = (popupStore.getTargetAgeGroup() != null) ? popupStore.getTargetAgeGroup() : existingPopup.getTargetAgeGroup();
        String targetLocation = (popupStore.getTargetLocation() != null) ? popupStore.getTargetLocation() : existingPopup.getTargetLocation();
        String title = (popupStore.getTitle() != null) ? popupStore.getTitle() : existingPopup.getTitle();
        Integer wishArea = (popupStore.getWishArea() != null) ? popupStore.getWishArea() : existingPopup.getWishArea();
        String description = (popupStore.getDescription() != null) ? popupStore.getDescription() : existingPopup.getDescription();
        LocalDate startDate = (popupStore.getStartDate() != null) ? popupStore.getStartDate() : existingPopup.getStartDate();
        LocalDate endDate = (popupStore.getEndDate() != null) ? popupStore.getEndDate() : existingPopup.getEndDate();

        // 3. 팝업스토어 업데이트
        int rowsUpdated = popupStoreJpaRepository.updatePopupStore(
                id, type, targetAgeGroup, targetLocation, title, wishArea, description, startDate, endDate
        );

        // 4. 이미지 업데이트
        List<PopupStoreImageList> newImgs = null;
        if (imgs != null && !imgs.isEmpty()) {
            // 기존 이미지 삭제
            popupStoreImageJpaRepository.deleteAll(existingImgs);

            // 새 이미지 저장
            newImgs = imgs.stream()
                    .map(image -> new PopupStoreImageList(new PopupStoreImageListId(id, image)))
                    .toList();
            popupStoreImageJpaRepository.saveAll(newImgs);
        }
        else {
            newImgs = existingImgs;
        }

        // 5. 결과 반환
        return new UpdatePopupStoreTO(rowsUpdated, newImgs);
    }

     */

    // 4. Delete : 팝업리스트 삭제
    public int delete(long seq) {
        try {
            // ID를 기반으로 팝업 스토어 삭제
            popupStoreJpaRepository.deleteById(seq);

            // 이미지 경로 설정
            String imgsPath = "src/main/resources/static/images/popup_detail";
            String thPath = "src/main/resources/static/images/popup_thumbnail";

            // 팝업 이미지 삭제
            deleteFilesInDirectory(imgsPath, "popup_" + seq + "_images_*");

            // 썸네일 이미지 삭제
            deleteFilesInDirectory(thPath, "popup_" + seq + "_thumbnail*");

            return 1;
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("알 수 없는 오류로 삭제 실패: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private void deleteFilesInDirectory(String dirPath, String pattern) throws IOException {
        Path path = Paths.get(dirPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, pattern)) {
            for (Path entry : stream) {
                File file = entry.toFile();
                if (file.exists() && file.isFile()) {
                    Files.delete(entry);  // 파일 삭제
                    System.out.println("파일 삭제 성공: " + entry.toString());
                } else {
                    System.out.println("파일이 존재하지 않거나 잘못된 경로입니다: " + entry.toString());
                }
            }
        }
    }
}
