package com.project.popupmarket.repository;

import com.project.popupmarket.entity.RentalPlace;
import com.project.popupmarket.entity.RentalPlaceImageList;
import com.project.popupmarket.entity.RentalPlaceImageListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalPlaceImageListJpaRepository extends JpaRepository<RentalPlaceImageList, RentalPlaceImageListId> {
    @Query("SELECT r FROM RentalPlaceImageList r WHERE r.rentalPlaceSeq.id = :rentalPlaceId")
    List<RentalPlaceImageList> findRentalPlaceImageList(@Param("rentalPlaceId") Long rentalPlaceId);
}
