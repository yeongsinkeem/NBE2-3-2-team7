package com.project.popupmarket.repository;

import com.project.popupmarket.entity.RentalPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RentalPlaceJpaRepository extends JpaRepository<RentalPlace, Long> {
    //jpql

    @Query(value = "SELECT r FROM RentalPlace r ORDER BY r.registeredAt DESC LIMIT 10")
    List<RentalPlace> findWithLimit();

    @Query("SELECT rp.id, rp.thumbnail, rp.address, rp.name, rp.status " +
            "FROM RentalPlace rp WHERE rp.rentalUserSeq.id = :userId")
    List<RentalPlace> findRentalPlacesByUserId(@Param("userId") Long userId);

    @Query("SELECT rp " +
            "FROM RentalPlace rp " +
            "WHERE rp.rentalUserSeq.id = :userId " +
            "AND rp.status = 'ACTIVE'" +
            "AND NOT EXISTS ( " +
            "    SELECT 1 " +
            "    FROM PlaceRequest pr " +
            "    WHERE pr.rentalPlaceSeq.id = rp.id " +
            "    AND pr.popupStoreSeq.id = :popupId" +
            ")")
    List<RentalPlace> findActivatedRentalPlacesByUserId(
            @Param("userId") Long userId,
            @Param("popupId") Long popupId
    );


    @Query(value = "SELECT rp " +
            "FROM RentalPlace rp " +
            "WHERE rp.status = 'ACTIVE' " +
            "AND CAST(rp.capacity as Integer) BETWEEN :minCapacity AND :maxCapacity " +
            "AND (:location IS NULL OR rp.address LIKE %:location%) " +
            "AND rp.price BETWEEN :minPrice AND :maxPrice " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM Receipt r " +
            "   WHERE r.rentalPlaceSeq = rp.id " +
            "   AND (r.startDate <= :endDate AND r.endDate >= :startDate) " +
            "   AND r.reservationStatus != 'CANCELED'" +
            ")" +
            "ORDER BY " +
            "CASE WHEN :sorting = 'registered_desc' THEN rp.registeredAt END DESC, " +
            "CASE WHEN :sorting = 'registered_asc' THEN rp.registeredAt END ASC, " +
            "CASE WHEN :sorting = 'area_desc' THEN rp.capacity END DESC, " +
            "CASE WHEN :sorting = 'area_asc' THEN rp.capacity END ASC, " +
            "CASE WHEN :sorting = 'price_desc' THEN rp.price END DESC, " +
            "CASE WHEN :sorting = 'price_asc' THEN rp.price END ASC, " +
            "CASE WHEN :sorting IS NULL OR :sorting = '' THEN rp.registeredAt END DESC," +
            "CASE WHEN :sorting NOT IN ('registered_desc', 'registered_asc') THEN rp.registeredAt END DESC"
        )
    Page<RentalPlace> findFilteredWithPagination(
            @Param("minCapacity") Integer  minCapacity,
            @Param("maxCapacity") Integer  maxCapacity,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("sorting") String sorting,
            Pageable pageable
    );

    @Query("SELECT r.rentalUserSeq.id FROM RentalPlace r WHERE r.id = :id")
    Long findUserSeqById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM RentalPlace r WHERE r.id = :id")
    void deleteRentalPlaceById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RentalPlace r SET r.status = :status WHERE r.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") String status);


}
