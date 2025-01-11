package com.project.popupmarket.service.invitation;

import com.project.popupmarket.dto.invitation.InvitationInfoTO;
import com.project.popupmarket.dto.invitation.InvitationTO;
import com.project.popupmarket.dto.rentalDto.RentalPlaceTO;
import com.project.popupmarket.entity.*;
import com.project.popupmarket.repository.PlaceRequestRepository;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvitationService {

    private final PopupStoreJpaRepository popupStoreJpaRepository;
    @PersistenceContext
    private EntityManager em;

    private final PlaceRequestRepository placeRequestRepository;

    @Autowired
    public InvitationService(PlaceRequestRepository placeRequestRepository, PopupStoreJpaRepository popupStoreJpaRepository) {
        this.placeRequestRepository = placeRequestRepository;
        this.popupStoreJpaRepository = popupStoreJpaRepository;
    }

    @Transactional
    public boolean addInvitation(InvitationTO invitation) {
        PlaceRequestId placeRequestId = new PlaceRequestId();
        new ModelMapper().map(invitation, placeRequestId);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setId(placeRequestId);

        RentalPlace rentalPlace = new RentalPlace();
        rentalPlace.setId(placeRequestId.getRentalPlaceSeq());
        placeRequest.setRentalPlaceSeq(rentalPlace);

        PopupStore popupStore = new PopupStore();
        popupStore.setId(placeRequestId.getPopupStoreSeq());
        placeRequest.setPopupStoreSeq(popupStore);

        Optional<PlaceRequest> placeRequestOptional = placeRequestRepository.findById(placeRequest.getId());

        if (placeRequestOptional.isPresent()) {
            return false;
        } else {
            placeRequestRepository.save(placeRequest);
            return true;
        }
    }

    @Transactional
    public boolean removeInvitation(InvitationTO invitation) {
        PlaceRequestId placeRequestId = new PlaceRequestId();
        new ModelMapper().map(invitation, placeRequestId);

        PlaceRequest placeRequest = new PlaceRequest();
        placeRequest.setId(placeRequestId);

        Optional<PlaceRequest> placeRequestOptional = placeRequestRepository.findById(placeRequest.getId());

        if (placeRequestOptional.isPresent()) {
            placeRequestRepository.delete(placeRequestOptional.get());
            return true;
        } else {
            return false;
        }
    }

    public InvitationInfoTO getInvitations(Long popupSeq) {
        QPlaceRequest qPlaceRequest = QPlaceRequest.placeRequest;
        JPAQuery<PlaceRequest> query = new JPAQuery<>(em);

        ModelMapper modelMapper = new ModelMapper();

        String popupTitle = popupStoreJpaRepository.findById(popupSeq).orElseThrow(EntityNotFoundException::new).getTitle();

        List<RentalPlaceTO> rentalPlace = query.select(qPlaceRequest.rentalPlaceSeq).from(qPlaceRequest)
                .where(qPlaceRequest.id.popupStoreSeq.eq(popupSeq)).fetch()
                .stream()
                .map(rp -> modelMapper.map(rp, RentalPlaceTO.class))
                .toList();

        return new InvitationInfoTO(popupTitle, rentalPlace);
    }
}
