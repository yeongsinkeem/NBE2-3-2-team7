package com.project.popupmarket.service.rentalService;

import com.project.popupmarket.dto.rentalDto.RentalPlaceImageTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import com.project.popupmarket.dto.rentalDto.UserRentalPlaceInfoTO;
import com.project.popupmarket.entity.PlaceRequestId;
import com.project.popupmarket.entity.RentalPlace;
import com.project.popupmarket.entity.RentalPlaceImageList;
import com.project.popupmarket.entity.RentalPlaceImageListId;
import com.project.popupmarket.repository.PlaceRequestRepository;
import com.project.popupmarket.repository.RentalPlaceImageListJpaRepository;
import com.project.popupmarket.repository.RentalPlaceJpaRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RentalPlaceServiceImpl {
    @Autowired
    private RentalPlaceJpaRepository rentalPlaceJpaRepository;
    @Autowired
    private RentalPlaceImageListJpaRepository rentalPlaceImageListJpaRepository;
    @Autowired
    private PlaceRequestRepository placeRequestRepository;

    public List<RentalPlaceTO> findWithLimit() {
        ModelMapper modelMapper = new ModelMapper();

        return rentalPlaceJpaRepository.findWithLimit()
                .stream()
                .map(p -> {
                    RentalPlaceTO rentalPlace = modelMapper.map(p, RentalPlaceTO.class);
                    rentalPlace.setThumbnail(
                            p.getThumbnail() != null ? p.getThumbnail() : "thumbnail_default.png"
                    );

                    return rentalPlace;
                }).toList();
    }

    public RentalPlaceTO findById(Long id) {

        Optional<RentalPlace> rentalPlace = rentalPlaceJpaRepository.findById(id);

        if (rentalPlace.isPresent()) {
            RentalPlaceTO rentalPlaceTO = new ModelMapper().map(rentalPlace.get(), RentalPlaceTO.class);
            rentalPlaceTO.setThumbnail(
                    (rentalPlace.get().getThumbnail() != null ?
                            rentalPlace.get().getThumbnail() : "thumbnail_default.png")
            );

            return rentalPlaceTO;
        }

        return null;
    }

    public String findPlaceThumbnailById(Long placeSeq) {
        return rentalPlaceJpaRepository.findById(placeSeq).get().getThumbnail();
    }

    public Page<RentalPlaceTO> findFilteredWithPagination(
            Integer minCapacity, Integer maxCapacity, String location,
            BigDecimal minPrice, BigDecimal maxPrice,
            LocalDate startDate, LocalDate endDate,
            String sorting, Pageable pageable) {
        ModelMapper modelMapper = new ModelMapper();

        return rentalPlaceJpaRepository
                .findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, startDate, endDate, sorting, pageable)
                .map(rp -> {
                    RentalPlaceTO rentalPlaceTO = modelMapper.map(rp, RentalPlaceTO.class);
                    rentalPlaceTO.setThumbnail(rp.getThumbnail() != null ?
                            rp.getThumbnail() : "thumbnail_default.png");

                    return rentalPlaceTO;
                });
    }

    public List<RentalPlaceImageTO> findRentalPlaceImageList (Long id) {

        List<RentalPlaceImageList> lists = rentalPlaceImageListJpaRepository.findRentalPlaceImageList(id);

        List<RentalPlaceImageTO> toList = new ArrayList<>();
        for (RentalPlaceImageList result : lists) {
            RentalPlaceImageTO to = new RentalPlaceImageTO();
            to.setRentalPlaceSeq(result.getId().getRentalPlaceSeq());
            to.setImage(result.getId().getImage());
            toList.add(to);
        }

        return toList;
    }

    public List<RentalPlaceTO> findRentalPlacesByUserId (Long userSeq) {
        ModelMapper modelMapper = new ModelMapper();

        return rentalPlaceJpaRepository.findRentalPlacesByUserId(userSeq)
                .stream().map(rp -> {
                    RentalPlaceTO to = modelMapper.map(rp, RentalPlaceTO.class);
                    to.setThumbnail(rp.getThumbnail() != null ?
                            rp.getThumbnail() : "thumbnail_default.png");

                    return to;
                })
                .toList();
    }

    public List<UserRentalPlaceInfoTO> findUserRentalPlaceInfo (Long userSeq, Long popupSeq) {
        ModelMapper modelMapper = new ModelMapper();

        return rentalPlaceJpaRepository.findActivatedRentalPlacesByUserId(userSeq, popupSeq)
                .stream().map(rp -> modelMapper.map(rp, UserRentalPlaceInfoTO.class))
                .toList();
    }

    public int insertRentalPlace(RentalPlaceTO to, MultipartFile thumbnail){
        int flag=0;
        to.setRegisteredAt(Instant.now());

        ModelMapper mapper = new ModelMapper();
        RentalPlace rentalPlace = mapper.map(to, RentalPlace.class);
        rentalPlace.setStatus("ACTIVE");

        RentalPlace savedPlace = rentalPlaceJpaRepository.save(rentalPlace);
        Long id = savedPlace.getId();
        Long userSeq = savedPlace.getRentalUserSeq().getId();

        String thumbnailName;
        if (thumbnail == null || thumbnail.isEmpty()) {
            savedPlace.setThumbnail(null);
        } else {
            String extension = "";
            String originalFilename = thumbnail.getOriginalFilename();
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            thumbnailName = String.format("place_%d_%d_thumbnail.%s", id, userSeq, extension);
            saveFile(thumbnail, "place_thumbnail", thumbnailName); // 파일 저장
            savedPlace.setThumbnail(thumbnailName);
        }
        rentalPlaceJpaRepository.save(savedPlace);

        return flag;
    }

    @Transactional
    public int updateRentalPlaceStatus(Long id, String status) {
        int flag = 0;

        rentalPlaceJpaRepository.updateStatusById(id, status);
        return flag;
    }

    public int insertRentalPlaceWithImages(
            RentalPlaceTO to,
            MultipartFile thumbnail,
            List<MultipartFile> images){

        int flag=0;
        to.setRegisteredAt(Instant.now());

        // RentalPlace 저장
        ModelMapper mapper = new ModelMapper();
        RentalPlace rentalPlace = mapper.map(to, RentalPlace.class);
        rentalPlace.setStatus("ACTIVE");

        RentalPlace savedPlace = rentalPlaceJpaRepository.save(rentalPlace);
        Long id = savedPlace.getId();
        Long userSeq = savedPlace.getRentalUserSeq().getId();

        // 썸네일 파일 저장
        String thumbnailName;
        if (thumbnail == null || thumbnail.isEmpty()) {
            thumbnailName = "thumbnail_default.png";
            savedPlace.setThumbnail(thumbnailName);
        } else {
            String extension = "";
            String originalFilename = thumbnail.getOriginalFilename();
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            thumbnailName = String.format("place_%d_%d_thumbnail.%s", id, userSeq, extension);
            saveFile(thumbnail, "place_thumbnail", thumbnailName); // 파일 저장
            savedPlace.setThumbnail(thumbnailName);
        }

        rentalPlaceJpaRepository.save(savedPlace);

        // 상세 이미지 저장
        List<RentalPlaceImageList> lists = new ArrayList<>();
        if (!images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile imageFile = images.get(i);

                String extension = "";
                String originalFilename = imageFile.getOriginalFilename();
                extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

                String imageName = String.format("place_%d_%d_images_%d.%s", id, userSeq, i + 1, extension);
                saveFile(imageFile, "place_detail", imageName);

                RentalPlaceImageListId imageListId = new RentalPlaceImageListId();
                imageListId.setRentalPlaceSeq(id);
                imageListId.setImage(imageName);

                RentalPlaceImageList imageList = new RentalPlaceImageList();
                imageList.setId(imageListId);
                imageList.setRentalPlaceSeq(savedPlace);

                lists.add(imageList);
            }

            rentalPlaceImageListJpaRepository.saveAll(lists);
        } else {
//            // 이미지가 비어있을 때 기본 이미지 추가 / 추가 x 상세 이미지는 무조건 들어와야 됨
//            String defaultImageName = "thumbnail_default.png";
//
//            RentalPlaceImageListId imageListId = new RentalPlaceImageListId();
//            imageListId.setRentalPlaceSeq(id);
//            imageListId.setImage(defaultImageName);
//
//            RentalPlaceImageList imageList = new RentalPlaceImageList();
//            imageList.setId(imageListId);
//            imageList.setRentalPlaceSeq(savedPlace);
//
//            lists.add(imageList);
        }

        return flag;
    }
    public int updateRentalPlaceImage(Long id, List<MultipartFile> images){
        int flag=0;

        RentalPlace savedPlace = rentalPlaceJpaRepository.findById(id).orElseThrow();
        Long userSeq = savedPlace.getRentalUserSeq().getId();

        // 이미지 데이터 저장
        List<RentalPlaceImageList> lists = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            // 이미지가 없을 때 기본 이미지 추가 -> null로 적용.
            String defaultImageName = "thumbnail_default.png";

            RentalPlaceImageListId imageListId = new RentalPlaceImageListId();
            imageListId.setRentalPlaceSeq(id);
            imageListId.setImage(defaultImageName);

            RentalPlaceImageList imageList = new RentalPlaceImageList();
            imageList.setId(imageListId);
            imageList.setRentalPlaceSeq(savedPlace);

            lists.add(imageList);
        } else {
            // 이미지가 있을 때 처리
            for (int i = 0; i < images.size(); i++) {

                String imageName = String.format("rental_%d_%d_images_%d.png", id, userSeq, i + 1);

                saveFile(images.get(i), "place_detail", imageName);

                RentalPlaceImageListId imageListId = new RentalPlaceImageListId();
                imageListId.setRentalPlaceSeq(id);
                imageListId.setImage(imageName);

                RentalPlaceImageList imageList = new RentalPlaceImageList();
                imageList.setId(imageListId);
                imageList.setRentalPlaceSeq(savedPlace);

                lists.add(imageList);
            }
        }

        rentalPlaceImageListJpaRepository.saveAll(lists);
        return flag;
    }

    @Transactional
    public int deleteRentalPlaceById(Long id){
        int flag=0;
        Long userSeq = rentalPlaceJpaRepository.findUserSeqById(id);

        deleteThumbnailFile(id, userSeq);
        deleteImageFiles(id, userSeq);

        placeRequestRepository.deletePlaceRequestsByRentalPlaceSeq(id);
        rentalPlaceImageListJpaRepository.deleteRentalPlaceImageBySeq(id);
        rentalPlaceJpaRepository.deleteRentalPlaceById(id);

        return flag;
    }

    @Transactional
    public int deleteRentalPlaceImageById(Long id){
        int flag=0;
        rentalPlaceImageListJpaRepository.deleteRentalPlaceImageBySeq(id);

        return flag;
    }

    private void saveFile(MultipartFile file, String folder, String filename) {
        String path = "C:/popupmarket/";
        try {
            String uploadDir = path + folder + "/";
            Path filePath = Paths.get(uploadDir + filename);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private void deleteThumbnailFile(Long id, Long userSeq) {
        try {
            String basePath = "C:/popupmarket/";
            String thumbnailPath = String.format(basePath + "place_thumbnail/place_%d_%d_thumbnail.png", id, userSeq);
            File thumbnailFile = new File(thumbnailPath);

            if (thumbnailFile.exists()) {
                if (thumbnailFile.delete()) {
                    System.out.println("썸네일 파일 삭제 성공: " + thumbnailPath);
                } else {
                    System.err.println("썸네일 파일 삭제 실패: " + thumbnailPath);
                }
            } else {
                System.out.println("삭제할 썸네일 파일이 존재하지 않음: " + thumbnailPath);
            }
        } catch (Exception e) {
            System.err.println("썸네일 파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }
    private void deleteImageFiles(Long id, Long userSeq) {
        try {
            String basePath = "C:/popupmarket/";
            String imagePathPattern = String.format(basePath + "place_detail/place_%d_%d_images_", id, userSeq);

            int index = 1;
            while (true) {
                String imagePath = String.format(imagePathPattern + "%d.png", index);
                File imageFile = new File(imagePath);

                if (imageFile.exists()) {
                    if (imageFile.delete()) {
                        System.out.println("상세 이미지 파일 삭제 성공: " + imagePath);
                    } else {
                        System.err.println("상세 이미지 파일 삭제 실패: " + imagePath);
                    }
                } else {
                    System.out.println("삭제할 이미지 파일이 더 이상 존재하지 않음 (index: " + index + ")");
                    break;
                }
                index++;
            }
        } catch (Exception e) {
            System.err.println("상세 이미지 파일 삭제 중 오류 발생: " + e.getMessage());
        }
    }

}
