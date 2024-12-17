package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// DB에 접근하여 CRUD와 같은 쿼리 실행
public interface PopupStoreJpaRepository extends JpaRepository<PopupStore, Long> {

    /*
    @Query(value = "select p.title, p.thumbnail, p.targetLocation, p.targetAgeGroup, p.wishArea, p.description " +
                    "from PopupStore p " +
                    "where p.seq = :id" )
    List<PopupStore> findPopupStoreBySeq(Long id);
     */

}
