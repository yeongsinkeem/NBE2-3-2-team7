package com.project.popupmarket.repository;

import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.entity.RentalPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RentalPlaceJpaRepository extends JpaRepository<RentalPlace, Long> {
    //jpql

    @Query(value = "SELECT seq as id, name, price, address, thumbnail  FROM rental_place ORDER BY seq LIMIT 10", nativeQuery = true)
    List<Object[]> findWithLimit();

    @Query("SELECT rp.thumbnail, rp.address, rp.name, rp.status " +
            "FROM RentalPlace rp WHERE rp.rentalUserSeq.id = :userId")
    List<Object[]> findRentalPlacesByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT rp.zipcode, rp.price, rp.address, rp.addrDetail, rp.description, " +
            "rp.infra, rp.name, rp.capacity, rp.nearbyAgeGroup, rp.registeredAt " +
            "FROM RentalPlace rp WHERE rp.id = :id")
    Object findDetailById(@Param("id") Long id);

    @Query(value = "SELECT rp.capacity, rp.price, rp.name, rp.thumbnail, rp.registeredAt " +
            "FROM RentalPlace rp " +
            "WHERE rp.status = 'ACTIVE' " +
            "AND CAST(rp.capacity as Integer) BETWEEN :minCapacity AND :maxCapacity " +
            "AND (:location IS NULL OR rp.address LIKE %:location%) " +
            "AND rp.price BETWEEN :minPrice AND :maxPrice")
    Page<Object[]> findFilteredWithPagination(
            @Param("minCapacity") Integer  minCapacity,
            @Param("maxCapacity") Integer  maxCapacity,
            @Param("location") String location,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );


}
