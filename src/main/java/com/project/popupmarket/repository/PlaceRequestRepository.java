package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PlaceRequest;
import com.project.popupmarket.entity.PlaceRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRequestRepository extends JpaRepository<PlaceRequest, PlaceRequestId> {
    @Modifying
    @Query("DELETE FROM PlaceRequest pr WHERE pr.rentalPlaceSeq.id = :rentalPlaceSeq")
    void deletePlaceRequestsByRentalPlaceSeq(@Param("rentalPlaceSeq") Long rentalPlaceSeq);
}
