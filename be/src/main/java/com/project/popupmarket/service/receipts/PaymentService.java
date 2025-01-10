package com.project.popupmarket.service.receipts;

import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.entity.*;
import com.project.popupmarket.enums.ReservationStatus;
import com.project.popupmarket.repository.ReceiptsRepository;
import com.project.popupmarket.repository.StagingPaymentRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PaymentService {

    @PersistenceContext
    private EntityManager em;

    private final ReceiptsRepository receiptsRepository;
    private final StagingPaymentRepository stagingPaymentRepository;

    @Autowired
    public PaymentService(ReceiptsRepository receiptsRepository, StagingPaymentRepository stagingPaymentRepository) {
        this.receiptsRepository = receiptsRepository;
        this.stagingPaymentRepository = stagingPaymentRepository;
    }

    public boolean receiptReservationDateCheck(ReservationTO reservation) {
        QReceipts qReceipt = QReceipts.receipts;
        JPAQuery<Receipts> query = new JPAQuery<>(em);

        return query.select(qReceipt)
                .from(qReceipt)
                .where(
                        qReceipt.rentalLandId.eq(reservation.getRentalLandId())
                                .and(qReceipt.startDate.between(reservation.getStartDate(), reservation.getEndDate())
                                    .or(qReceipt.endDate.between(reservation.getStartDate(), reservation.getEndDate()))
                                ).and(qReceipt.reservationStatus.ne(ReservationStatus.CANCELED))
                ).fetch().isEmpty();
    }

    public ReservationInfoResponse getPaymentInfo(ReservationTO reservation) {
        boolean flag = receiptReservationDateCheck(reservation);

        QUser qUser = QUser.user;
        QRentalLand qRentalLand = QRentalLand.rentalLand;

        JPAQuery<User> userJPAQuery = new JPAQuery<>(em);
        JPAQuery<RentalLand> placeJPAQuery = new JPAQuery<>(em);

        if (flag) {
            User user = userJPAQuery.select(qUser).from(qUser)
                    .where(qUser.id.eq(reservation.getCustomerId()))
                    .fetchOne();

            RentalLand rentalLand = placeJPAQuery.select(qRentalLand).from(qRentalLand)
                    .where(qRentalLand.id.eq(reservation.getRentalLandId()))
                    .fetchOne();

            return ReservationInfoResponse.builder()
                            .customerKey(UUID.randomUUID().toString())
                            .placeName(rentalLand.getTitle())
                            .price(rentalLand.getPrice())
                            .userEmail(user.getEmail())
                            .userName(user.getName())
                            .userTel(user.getTel())
                            .zipcode(rentalLand.getZipcode())
                            .address(rentalLand.getAddress())
                            .addrDetail(rentalLand.getAddrDetail())
                            .build();
        } else {
            return null;
        }
    }

    @Transactional
    public boolean insertStagingPayment(ReceiptsTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        ModelMapper modelMapper = new ModelMapper();
        StagingPayment stagingPayment = modelMapper.map(receipt, StagingPayment.class);
        StagingPayment saved = stagingPaymentRepository.save(stagingPayment);

        return saved.getOrderId() != null && saved.getOrderId().equals(stagingPayment.getOrderId());
    }

    @Transactional
    public boolean insertReceipt(ReceiptsTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        Receipts mapped = new ModelMapper().map(receipt, Receipts.class);

        Optional<StagingPayment> stagingPaymentOptional = stagingPaymentRepository.findById(receipt.getOrderId());

        if (stagingPaymentOptional.isPresent()){
            new ModelMapper().map(stagingPaymentOptional.get(), mapped);
            mapped.setReservationStatus(ReservationStatus.COMPLETED);
            Receipts saved = receiptsRepository.save(mapped);
            stagingPaymentRepository.delete(stagingPaymentOptional.get());

            return saved.getOrderId() != null && saved.getOrderId().equals(receipt.getOrderId());
        }

        return false;
    }

    @Transactional
    public boolean deleteStagingPayment(ReceiptsTO receipt) {
        Optional<StagingPayment> stagingPaymentOptional = stagingPaymentRepository.findById(receipt.getOrderId());

        if (stagingPaymentOptional.isPresent()) {
            StagingPayment stagingPayment = stagingPaymentOptional.get();

            stagingPaymentRepository.delete(stagingPayment);
            return true;
        }

        return false;
    }

    public List<ReceiptsInfoTO> getReceiptsByPlaceSeq(Long placeSeq) {
        QReceipts qReceipts = QReceipts.receipts;
        JPAQuery<Receipts> receiptJPAQuery = new JPAQuery<>(em);

        QRentalLand qRentalPlace = QRentalLand.rentalLand;
        QUser qUser = QUser.user;

        List<ReceiptsInfoTO> receiptInfoList = new ArrayList<>();

        receiptJPAQuery.select(qReceipts)
                .from(qReceipts)
                .where(qReceipts.rentalLandId.eq(placeSeq).and(
                        qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED)
                ))
                .orderBy(qReceipts.reservedAt.desc())
                .fetch()
                .forEach(item -> {
                    ReceiptsInfoTO receiptsInfoTO = new ModelMapper().map(item, ReceiptsInfoTO.class);

                    JPAQuery<RentalLand> rentalPlaceJPAQuery = new JPAQuery<>(em);
                    JPAQuery<User> userJPAQuery = new JPAQuery<>(em);

                    receiptsInfoTO.setRentalPlaceName(
                            rentalPlaceJPAQuery.select(qRentalPlace.title)
                                    .from(qRentalPlace)
                                    .where(qRentalPlace.id.eq(item.getRentalLandId()))
                                    .fetchOne()
                    );

                    receiptsInfoTO.setReservationUserName(
                            userJPAQuery.select(qUser.name)
                                    .from(qUser)
                                    .where(qUser.id.eq(item.getCustomerId()))
                                    .fetchOne()
                    );

                    receiptsInfoTO.setPrice(
                            receiptsInfoTO.getTotalAmount().divide(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(receiptsInfoTO.getStartDate(), receiptsInfoTO.getEndDate()) + 1
                                    ),0, RoundingMode.UP
                            )
                    );
                    receiptsInfoTO.setReservationStatus(item.getReservationStatus().getDesc());

                    receiptInfoList.add(receiptsInfoTO);
                });
        return receiptInfoList;
    }

    public List<ReceiptsInfoTO> getReceiptsByUserSeq(Long userSeq) {
        QReceipts qReceipts = QReceipts.receipts;
        JPAQuery<Receipts> receiptJPAQuery = new JPAQuery<>(em);

        QRentalLand qRentalPlace = QRentalLand.rentalLand;

        List<ReceiptsInfoTO> receiptInfoList = new ArrayList<>();

        receiptJPAQuery.select(qReceipts)
                .from(qReceipts)
                .where(qReceipts.customerId.eq(userSeq))
                .orderBy(qReceipts.reservedAt.desc())
                .fetch()
                .forEach(item -> {
                    ReceiptsInfoTO receiptsInfoTO = new ReceiptsInfoTO();
                    new ModelMapper().map(item, receiptsInfoTO);
                    JPAQuery<RentalLand> rentalPlaceJPAQuery = new JPAQuery<>(em);

                    receiptsInfoTO.setRentalPlaceName(
                            rentalPlaceJPAQuery.select(qRentalPlace.title)
                                    .from(qRentalPlace)
                                    .where(qRentalPlace.id.eq(item.getRentalLandId()))
                                    .fetchOne()
                    );
                    receiptsInfoTO.setPrice(
                        receiptsInfoTO.getTotalAmount().divide(
                            BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(receiptsInfoTO.getStartDate(), receiptsInfoTO.getEndDate()) + 1
                            ),0, RoundingMode.UP
                        )
                    );
                    receiptsInfoTO.setReservationStatus(item.getReservationStatus().getDesc());

                    receiptInfoList.add(receiptsInfoTO);
                });

        return receiptInfoList;
    }

    @Transactional
    public TossPaymentTO changeReservationStatus(Long userId, String orderId) {
        QReceipts qReceipts = QReceipts.receipts;
        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, qReceipts);
        JPAQuery<Receipts> query = new JPAQuery<>(em);
        Receipts receipts = query.select(qReceipts).from(qReceipts)
                        .where(qReceipts.orderId.eq(orderId)).fetchOne();

        jpaUpdateClause.set(qReceipts.reservationStatus, ReservationStatus.CANCELED)
                .where(qReceipts.orderId.eq(orderId)
                        .and(qReceipts.customerId.eq(userId))
                        .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED)))
                .execute();

        if (receipts != null) {
            return new ModelMapper().map(receipts, TossPaymentTO.class);
        } else {
            return null;
        }
    }

    public List<RangeDateTO> getRangeDates(Long rentalPlaceSeq) {
        QReceipts qReceipts = QReceipts.receipts;
        JPAQuery<Receipts> query = new JPAQuery<>(em);

        return query.select(Projections.constructor(RangeDateTO.class, qReceipts.startDate, qReceipts.endDate)).from(qReceipts)
                .where(
                    qReceipts.rentalLandId.eq(rentalPlaceSeq)
                    .and(qReceipts.reservationStatus.eq(ReservationStatus.COMPLETED))
                    .and(qReceipts.startDate.goe(LocalDate.now()))
                ).fetch();
    }
}