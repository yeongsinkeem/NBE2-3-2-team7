package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PopupStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// DB에 접근하여 CRUD와 같은 쿼리 실행
public interface PopupStoreJpaRepository extends JpaRepository<PopupStore, Long> {
    @Query("SELECT p FROM PopupStore p WHERE " +
            "(:type IS NULL OR p.type = :type) AND " +
            "(:targetAgeGroup IS NULL OR p.ageGroup = :targetAgeGroup) AND " +
            "(:startDate IS NULL OR p.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR p.endDate <= :endDate)" +
            "ORDER BY " +
            "CASE WHEN :sorting = 'registered_desc' THEN p.registeredAt END DESC, " +
            "CASE WHEN :sorting = 'registered_asc' THEN p.registeredAt END ASC, " +
            "CASE WHEN :sorting IS NULL OR :sorting = '' THEN p.registeredAt END DESC," +
            "CASE WHEN :sorting NOT IN ('registered_desc', 'registered_asc') THEN p.registeredAt END DESC"
    )
    Page<PopupStore> findByFilter(
            @Param("targetLocation") String targetLocation,
            @Param("type") String type,
            @Param("targetAgeGroup") String targetAgeGroup,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("sorting") String sorting,
            Pageable pageable
    );

    @Query("SELECT p FROM PopupStore p " +
            "WHERE p.customerId = :userSeq " +
            "ORDER BY p.registeredAt DESC")
    List<PopupStore> findByUserSeq(@Param("userSeq") Long userSeq);

    @Query("SELECT p.id FROM PopupStore p " +
            "ORDER BY p.id DESC LIMIT 1")
    Long findLastSeq();

    @Modifying
    @Query("UPDATE PopupStore p " +
            "SET p.type = COALESCE(:type, p.type), " +
            "p.ageGroup = COALESCE(:targetAgeGroup, p.ageGroup), " +
            "p.title = COALESCE(:title, p.title), " +
            "p.description = COALESCE(:description, p.description), " +
            "p.startDate = COALESCE(:startDate, p.startDate), " +
            "p.endDate = COALESCE(:endDate, p.endDate) " +
            "WHERE p.id = :id")
    int updatePopupStore(
            @Param("id") Long id,
            @Param("type") String type,
            @Param("targetAgeGroup") String ageGroup,
            @Param("title") String title,
            @Param("description") String description,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT p FROM PopupStore p " +
            "ORDER BY p.registeredAt DESC " +
            "LIMIT 10")
    List<PopupStore> findByLimit();
}
