package com.project.popupmarket.repository;

import com.project.popupmarket.entity.PlaceRequest;
import com.project.popupmarket.entity.PlaceRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRequestRepository extends JpaRepository<PlaceRequest, PlaceRequestId> {
}
