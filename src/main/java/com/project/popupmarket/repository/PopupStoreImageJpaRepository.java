package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PopupStoreImageList;
import com.project.popupmarket.entity.PopupStoreImageListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupStoreImageJpaRepository extends JpaRepository<PopupStoreImageList, PopupStoreImageListId> {
    // 특정 popupStoreSeq에 해당하는 이미지 목록 조회
    @Query("SELECT pi.id.image FROM PopupStoreImageList pi WHERE pi.id.popupStoreSeq = :popupStoreSeq")
    List<String> findById_PopupStoreSeq(@Param("popupStoreSeq") Long popupStoreSeq);

    @Modifying
    @Query("UPDATE PopupStoreImageList pi " +
            "SET pi.id.image = COALESCE(:image, pi.id.image)" +
            "WHERE pi.id = :id")
    int updatePopupStoreImg(
            @Param("id") PopupStoreImageListId id,
            @Param("image") String image
    );
}
