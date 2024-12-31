package com.project.popupmarket.service.invitation;

import com.project.popupmarket.dto.invitation.InvitationTO;
import com.project.popupmarket.entity.PlaceRequest;
import com.project.popupmarket.entity.PlaceRequestId;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.entity.RentalPlace;
import com.project.popupmarket.repository.PlaceRequestRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvitationService {

    @PersistenceContext
    private EntityManager em;

    private final PlaceRequestRepository placeRequestRepository;

    @Autowired
    public InvitationService(PlaceRequestRepository placeRequestRepository) {
        this.placeRequestRepository = placeRequestRepository;
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

//    public List<InvitationInfoTO> getInvitations(Long popupSeq) {
//        QPlaceRequest qPlaceRequest = QPlaceRequest.placeRequest;
//        JPAQuery<PlaceRequest> query = new JPAQuery<>(em);
//        List<PlaceRequest> placeRequests = query.select(qPlaceRequest).from(qPlaceRequest)
//                .where(qPlaceRequest.id.popupStoreSeq.eq(popupSeq)).fetch();
//
//
//    }
}
