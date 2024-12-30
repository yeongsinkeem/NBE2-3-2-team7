package com.project.popupmarket.service.popupService;

import com.project.popupmarket.dto.popupDto.PopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.entity.PopupStoreImageList;
import com.project.popupmarket.entity.PopupStoreImageListId;
import com.project.popupmarket.repository.PopupStoreImageJpaRepository;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopupStoreServiceImpl {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;
    @Autowired
    private PopupStoreImageJpaRepository popupStoreImageJpaRepository;
    @Autowired
    private PopupStoreFileStorageService popupStoreFileStorageService;

    // 1. Create : 팝업스토어 추가
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

    // 3 - 1 : Update PopupStore
    @Transactional
    public int updatePopup(Long seq, PopupStoreTO to, MultipartFile thumbnail) {
        // 1. 기존 팝업스토어 가져오기
        PopupStore existingPopupStore = popupStoreJpaRepository.findById(seq)
                .orElseThrow(() -> new EntityNotFoundException("PopupStore not found for ID: " + seq));

        // 2. 값 설정 : null이 아닌 값만 업데이트
        String type = (to.getType() != null && !to.getType().isBlank()) ? to.getType() : existingPopupStore.getType();
        String targetAgeGroup = (to.getTargetAgeGroup() != null && !to.getTargetAgeGroup().isBlank()) ? to.getTargetAgeGroup() : existingPopupStore.getTargetAgeGroup();
        String targetLocation = (to.getTargetLocation() != null && !to.getTargetLocation().isBlank()) ? to.getTargetLocation() : existingPopupStore.getTargetLocation();
        String title = (to.getTitle() != null && !to.getTitle().isBlank()) ? to.getTitle() : existingPopupStore.getTitle();
        Integer wishArea = (to.getWishArea() != null) ? to.getWishArea() : existingPopupStore.getWishArea();
        String description = (to.getDescription() != null && !to.getDescription().isBlank()) ? to.getDescription() : existingPopupStore.getDescription();
        LocalDate startDate = (to.getStartDate() != null) ? to.getStartDate() : existingPopupStore.getStartDate();
        LocalDate endDate = (to.getEndDate() != null) ? to.getEndDate() : existingPopupStore.getEndDate();

        // 3. 썸네일 처리
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thPath = "src/main/resources/static/";
            String existingThName = existingPopupStore.getThumbnail();
            System.out.println("매개변수로 받은 썸네일 있습니당.");

            try {
                // 기존 썸네일 삭제
                if (existingThName != null) {
                    String deleteThPath = thPath + "/" + existingThName;
                    System.out.println("삭제 파일은 : " + deleteThPath);
                    boolean deleted = popupStoreFileStorageService.deleteFile(deleteThPath);
                    if (!deleted) {
                        throw new RuntimeException("Failed to delete existing thumbnail: " + existingThName);
                    }
                }

                // 새로운 썸네일을 기존 이름으로 저장
                popupStoreFileStorageService.storeFile(thumbnail, thPath, existingThName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store thumbnail with name: " + existingThName, e);
            }
        }
        else {
            System.out.println("받은 썸네일 없음");
        }

        // 4. 업데이트 쿼리 실행
        return popupStoreJpaRepository.updatePopupStore(
                seq, type, targetAgeGroup, targetLocation, title, wishArea, description, startDate, endDate
        );
    }

    // 3 - 2 : Update PopupStoreImageList
    @Transactional
    public int updatePopupImgs(Long seq, List<MultipartFile> newImgs) {
        // 1. 기존 팝업스토어 가져오기
        PopupStore existingPopupStore = popupStoreJpaRepository.findById(seq)
                .orElseThrow(() -> new EntityNotFoundException("PopupStore not found for ID: " + seq));


        String imgPath = "src/main/resources/static/";

        // 2. 기존 이미지 리스트 가져오기
        List<String> existingImgs = popupStoreImageJpaRepository.findById_PopupStoreSeq(seq);

        try {
            // 3. 새 이미지 없다면 기존 이미지 유지
            if (newImgs == null || newImgs.isEmpty()) {
                return 0;
            }

            // 4. 새 이미지 있다면 기존 이미지 삭제
            if (existingImgs != null && !existingImgs.isEmpty()) {
                for (String img : existingImgs) {
                    System.out.println("기존 이미지 이름 : " + img);
                    String deleteImgPath = imgPath + "/" + img;
                    // 파일 경로에서 삭제
                    boolean deleted = popupStoreFileStorageService.deleteFile(deleteImgPath);
                    if (!deleted) {
                        throw new RuntimeException("Failed to delete existing img: " + img);
                    }
                }
                // DB에서 삭제
                popupStoreImageJpaRepository.deleteAllById(seq);
            }

            // 5. 새로운 이미지 저장
            List<PopupStoreImageList> newImageEntities = new ArrayList<>();
            int i = 1;
            for (MultipartFile newImg : newImgs) {
                if (!newImg.isEmpty()) {
                    // 이미지 이름 지정
                    String originalName = newImg.getOriginalFilename();
                    String ext = originalName.substring(originalName.lastIndexOf(".") + 1);
                    String newImgName = "popup_" + (seq) + "_images_" + i + "." + ext;

                    // 파일 저장
                    popupStoreFileStorageService.storeFile(newImg, imgPath + "images/popup_detail/", newImgName);

                    // 이미지 엔티티 생성
                    PopupStoreImageList newImage = new PopupStoreImageList();
                    newImage.setId(new PopupStoreImageListId(seq, newImgName));
                    newImageEntities.add(newImage);

                    i++;
                }
            }

            // 6. 새 이미지 레코드 삽입
            popupStoreImageJpaRepository.saveAll(newImageEntities);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update popup images for PopupStore ID: " + seq, e);
        }
        return 1;
    }



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
