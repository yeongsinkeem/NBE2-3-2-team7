package com.project.popupmarket.repository;

import com.project.popupmarket.entity.RentalLand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RentalLandJpaRepository extends JpaRepository<RentalLand, Long> {
    //jpql

    @Query(value = "SELECT rp FROM RentalLand rp ORDER BY rp.registeredAt DESC LIMIT 10")
    List<RentalLand> findWithLimit();

    @Query("SELECT rp " +
            "FROM RentalLand rp " +
            "WHERE rp.landlordId = :userId " +
            "ORDER BY rp.registeredAt DESC")
    List<RentalLand> findRentalPlacesByUserId(@Param("userId") Long userId);

    @Query("SELECT rp " +
            "FROM RentalLand rp " +
            "WHERE rp.landlordId = :userId " +
            "AND rp.status = 'active'")
    List<RentalLand> findActivatedRentalPlacesByUserId(
            @Param("userId") Long userId,
            @Param("popupId") Long popupId
    );


    @Query(value = "SELECT rp " +
            "FROM RentalLand rp " +
            "WHERE rp.status = 'ACTIVE' " +
            "AND rp.area BETWEEN :minCapacity AND :maxCapacity " +
            "AND (:location IS NULL OR rp.address LIKE %:location%) " +
            "AND rp.price BETWEEN :minPrice AND :maxPrice " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM Receipts r " +
            "   WHERE r.rentalLandId = rp.id " +
            "   AND (r.startDate <= :endDate AND r.endDate >= :startDate) " +
            "   AND r.reservationStatus != 'CANCELED'" +
            ")" +
            "ORDER BY " +
            "CASE WHEN :sorting = 'registered_desc' THEN rp.registeredAt END DESC, " +
            "CASE WHEN :sorting = 'registered_asc' THEN rp.registeredAt END ASC, " +
            "CASE WHEN :sorting = 'area_desc' THEN rp.area END DESC, " +
            "CASE WHEN :sorting = 'area_asc' THEN rp.area END ASC, " +
            "CASE WHEN :sorting = 'price_desc' THEN rp.price END DESC, " +
            "CASE WHEN :sorting = 'price_asc' THEN rp.price END ASC, " +
            "CASE WHEN :sorting IS NULL OR :sorting = '' THEN rp.registeredAt END DESC," +
            "CASE WHEN :sorting NOT IN ('registered_desc', 'registered_asc') THEN rp.registeredAt END DESC"
        )
    Page<RentalLand> findFilteredWithPagination(
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

    @Query("SELECT r.landlordId FROM RentalLand r WHERE r.id = :id")
    Long findUserSeqById(@Param("id") Long id);

    @Modifying
    @Query("DELETE FROM RentalLand r WHERE r.id = :id")
    void deleteRentalPlaceById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RentalLand r SET r.status = :status WHERE r.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") String status);


}
