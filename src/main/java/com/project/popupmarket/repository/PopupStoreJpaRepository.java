package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PopupStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// DB에 접근하여 CRUD와 같은 쿼리 실행
public interface PopupStoreJpaRepository extends JpaRepository<PopupStore, Long> {
    /*
    @Query(value = "select p.title, p.thumbnail, p.targetLocation, p.targetAgeGroup, p.wishArea, p.description " +
                    "from PopupStore p " +
                    "where p.seq = :id" )
    List<PopupStore> findPopupStoreBySeq(Long id);
     */

    @Query("SELECT p FROM PopupStore p WHERE " +
            "(:targetLocation IS NULL OR p.targetLocation = :targetLocation) AND " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:targetAgeGroup IS NULL OR p.targetAgeGroup = :targetAgeGroup) AND " +
            "(:startDate IS NULL OR p.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.endDate <= :endDate)")
    List<PopupStore> findByFilter(
            @Param("targetLocation") String targetLocation,
            @Param("type") String type,
            @Param("targetAgeGroup") String targetAgeGroup,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("UPDATE PopupStore p " +
            // "SET p.thumbnail = COALESCE(:thumbnail, p.thumbnail), " +
            "SET p.type = COALESCE(:type, p.type), " +
            "p.targetAgeGroup = COALESCE(:targetAgeGroup, p.targetAgeGroup), " +
            "p.targetLocation = COALESCE(:targetLocation, p.targetLocation), " +
            "p.title = COALESCE(:title, p.title), " +
            "p.wishArea = COALESCE(:wishArea, p.wishArea), " +
            "p.description = COALESCE(:description, p.description), " +
            "p.startDate = COALESCE(:startDate, p.startDate), " +
            "p.endDate = COALESCE(:endDate, p.endDate) " +
            "WHERE p.id = :id")
    int updatePopupStore(
            @Param("id") Long id,
            //@Param("thumbnail") String thumbnail,
            @Param("type") String type,
            @Param("targetAgeGroup") String targetAgeGroup,
            @Param("targetLocation") String targetLocation,
            @Param("title") String title,
            @Param("wishArea") Integer wishArea,
            @Param("description") String description,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
