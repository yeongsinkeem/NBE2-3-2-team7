package com.project.popupmarket.service;

import com.project.popupmarket.dto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.entity.RentalPlace;
import com.project.popupmarket.entity.RentalPlaceImageList;
import com.project.popupmarket.entity.RentalPlaceImageListId;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class RentalPlaceServiceImpl {
    @Autowired
    private RentalPlaceJpaRepository rentalPlaceJpaRepository;
    @Autowired
    private RentalPlaceImageListJpaRepository rentalPlaceImageListJpaRepository;

    public List<RentalPlaceTO> findWithLimit() {
        List<Object[]> lists = rentalPlaceJpaRepository.findWithLimit();

        List<RentalPlaceTO> to = new ArrayList<>();

        for (Object[] result : lists) {
            RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
            rentalPlaceTO.setId((Long) result[0]); // seq
            rentalPlaceTO.setName((String) result[1]);                 // name
            rentalPlaceTO.setPrice((BigDecimal) result[2]);            // price
            rentalPlaceTO.setAddress((String) result[3]);              // address
            rentalPlaceTO.setThumbnail(result[4] != null
                    ? "/images/place_thumbnail/" + result[4]
                    : null);                                           // thumbnail
            to.add(rentalPlaceTO);
        }

        return to;
    }

    public RentalPlaceTO findById(Long id) {

        RentalPlace rentalPlace = rentalPlaceJpaRepository.findById(id).get();

        ModelMapper mapper = new ModelMapper();
        RentalPlaceTO to = mapper.map(rentalPlace, RentalPlaceTO.class);

        to.setThumbnail((rentalPlace.getThumbnail() != null
                ? "/images/place_thumbnail/" + rentalPlace.getThumbnail() : null));

        return to;
    }

    public RentalPlaceTO findDetailById(Long id) {
        Object result = rentalPlaceJpaRepository.findDetailById(id);

        Object[] objects = (Object[]) result;

        RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
        rentalPlaceTO.setZipcode((String) objects[0]);
        rentalPlaceTO.setPrice((BigDecimal) objects[1]);
        rentalPlaceTO.setAddress((String) objects[2]);
        rentalPlaceTO.setAddrDetail((String) objects[3]);
        rentalPlaceTO.setDescription((String) objects[4]);
        rentalPlaceTO.setInfra((String) objects[5]);
        rentalPlaceTO.setName((String) objects[6]);
        rentalPlaceTO.setCapacity((String) objects[7]);
        rentalPlaceTO.setNearbyAgeGroup((String) objects[8]);
        rentalPlaceTO.setRegisteredAt((Instant) objects[9]);

        return rentalPlaceTO;
    }

    public Page<RentalPlaceTO> findFilteredWithPagination(
            Integer minCapacity, Integer maxCapacity, String location,
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        Page<Object[]> lists = rentalPlaceJpaRepository.findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, pageable);
        System.out.println(lists);
        System.out.println("Content: " + lists.getContent());

//        rp.Capacity, rp.price, rp.name, rp.thumbnail, rp.registeredAt
        Page<RentalPlaceTO> to = lists.map(objects -> {
            RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
            rentalPlaceTO.setCapacity((String) objects[0]);
            rentalPlaceTO.setPrice((BigDecimal) objects[1]);
            rentalPlaceTO.setName((String) objects[2]);
            rentalPlaceTO.setThumbnail(objects[3] != null
                    ? "/images/place_thumbnail/" + objects[3]
                    : null);
            rentalPlaceTO.setRegisteredAt((Instant) objects[4]);
            return rentalPlaceTO;
        });

        return to;
    }

    public List<RentalPlaceImageListTO> findRentalPlaceImageList (Long id) {

        List<RentalPlaceImageList> lists = rentalPlaceImageListJpaRepository.findRentalPlaceImageList(id);

        List<RentalPlaceImageListTO> toList = new ArrayList<>();
        for (RentalPlaceImageList result : lists) {
            RentalPlaceImageListTO to = new RentalPlaceImageListTO();
            to.setRentalPlaceSeq(result.getId().getRentalPlaceSeq());
            to.setImage("/images/place_details/" + result.getId().getImage());
            toList.add(to);
        }

        return toList;
    }

    public List<RentalPlaceTO> findRentalPlacesByUserId (Long id) {
        List<Object[]> results = rentalPlaceJpaRepository.findRentalPlacesByUserId(id);

        List<RentalPlaceTO> toList = new ArrayList<>();
        for (Object[] result : results) {
            RentalPlaceTO to = new RentalPlaceTO();
            to.setId((Long) result[0]);
            to.setThumbnail(result[1] != null
                    ? "/images/place_thumbnail/" + (String) result[1]
                    : null);
            to.setAddress((String) result[2]);
            to.setName((String) result[3]);
            to.setStatus((String) result[4]);
            toList.add(to);
        }

        return toList;
    }
    public int insertRentalPlace(RentalPlaceTO to, MultipartFile thumbnail){
        int flag=0;
        to.setRegisteredAt(Instant.now());

        ModelMapper mapper = new ModelMapper();
        RentalPlace rentalPlace = mapper.map(to, RentalPlace.class);

        RentalPlace savedPlace = rentalPlaceJpaRepository.save(rentalPlace);
        Long id = savedPlace.getId();
        Long userSeq = savedPlace.getRentalUserSeq().getId();

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

        return flag;
    }

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
        if (images == null || images.isEmpty()) {
            // 이미지가 비어있을 때 기본 이미지 추가
            String defaultImageName = "thumbnail_default.png";

            RentalPlaceImageListId imageListId = new RentalPlaceImageListId();
            imageListId.setRentalPlaceSeq(id);
            imageListId.setImage(defaultImageName);

            RentalPlaceImageList imageList = new RentalPlaceImageList();
            imageList.setId(imageListId);
            imageList.setRentalPlaceSeq(savedPlace);

            lists.add(imageList);
        } else {
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
        }

        rentalPlaceImageListJpaRepository.saveAll(lists);

        return flag;
    }
    public int updateRentalPlaceImage(Long id, List<MultipartFile> images){
        int flag=0;

        RentalPlace savedPlace = rentalPlaceJpaRepository.findById(id).orElseThrow();
        Long userSeq = savedPlace.getRentalUserSeq().getId();

        // 이미지 데이터 저장
        List<RentalPlaceImageList> lists = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            // 이미지가 없을 때 기본 이미지 추가
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
                // 파일명 생성
                String imageName = String.format("rental_%d_%d_images_%d.png", id, userSeq, i + 1);

                // 파일 저장
                saveFile(images.get(i), "place_detail", imageName);

                // DB 저장
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
        String path = "C:/Users/Kang/Java/SpringProjects/NBE2-3-2-team7/src/main/resources/static/images/";
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
            String basePath = "C:/Users/Kang/Java/SpringProjects/NBE2-3-2-team7/src/main/resources/static/images/";
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
            String basePath = "C:/Users/Kang/Java/SpringProjects/NBE2-3-2-team7/src/main/resources/static/images/";
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
