package com.project.popupmarket.service.receipts;

import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.entity.*;
import com.project.popupmarket.repository.ReceiptRepository;
import com.project.popupmarket.repository.StagingPaymentRepository;
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

import static com.project.popupmarket.entity.Receipt.ReservationStatus;

@Service
public class PaymentService {

    @PersistenceContext
    private EntityManager em;

    private final ReceiptRepository receiptRepository;
    private final StagingPaymentRepository stagingPaymentRepository;

    @Autowired
    public PaymentService(ReceiptRepository receiptRepository, StagingPaymentRepository stagingPaymentRepository) {
        this.receiptRepository = receiptRepository;
        this.stagingPaymentRepository = stagingPaymentRepository;
    }

    public boolean receiptReservationDateCheck(ReservationTO reservation) {
        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> query = new JPAQuery<>(em);

        return query.select(qReceipt)
                .from(qReceipt)
                .where(
                        qReceipt.rentalPlaceSeq.eq(reservation.getRentalPlaceSeq())
                                .and(qReceipt.startDate.between(reservation.getStartDate(), reservation.getEndDate())
                                    .or(qReceipt.endDate.between(reservation.getStartDate(), reservation.getEndDate()))
                                ).and(qReceipt.reservationStatus.ne(ReservationStatus.CANCELED))
                ).fetch().isEmpty();
    }

    public ReservationInfoTO getPaymentInfo(ReservationTO reservation) {
        boolean flag = receiptReservationDateCheck(reservation);

        QUser qUser = QUser.user;
        QRentalPlace qRentalPlace = QRentalPlace.rentalPlace;

        JPAQuery<User> userJPAQuery = new JPAQuery<>(em);
        JPAQuery<RentalPlace> placeJPAQuery = new JPAQuery<>(em);

        if (flag) {
            ReservationInfoTO reservationInfoTO = new ReservationInfoTO();

            User user = userJPAQuery.select(qUser).from(qUser)
                    .where(qUser.id.eq(reservation.getUserSeq()))
                    .fetchOne();

            RentalPlace rentalPlace = placeJPAQuery.select(qRentalPlace).from(qRentalPlace)
                    .where(qRentalPlace.id.eq(reservation.getRentalPlaceSeq()))
                    .fetchOne();

            reservationInfoTO.setCustomerKey(UUID.randomUUID().toString());
            reservationInfoTO.setPlaceName(rentalPlace.getName());
            reservationInfoTO.setPrice(rentalPlace.getPrice());
            reservationInfoTO.setUserEmail(user.getEmail());
            reservationInfoTO.setUserName(user.getName());
            reservationInfoTO.setUserTel(user.getTel());
            reservationInfoTO.setArea(rentalPlace.getZipcode());
            reservationInfoTO.setAddress(rentalPlace.getAddress());
            reservationInfoTO.setAddrDetail(rentalPlace.getAddrDetail());

            return reservationInfoTO;
        } else {
            return null;
        }
    }

    @Transactional
    public boolean insertStagingPayment(ReceiptTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        ModelMapper modelMapper = new ModelMapper();
        StagingPayment stagingPayment = modelMapper.map(receipt, StagingPayment.class);
        StagingPayment saved = stagingPaymentRepository.save(stagingPayment);

        return saved.getOrderId() != null && saved.getOrderId().equals(stagingPayment.getOrderId());
    }

    @Transactional
    public boolean insertReceipt(ReceiptTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        Receipt mapped = new ModelMapper().map(receipt, Receipt.class);

        Optional<StagingPayment> stagingPaymentOptional = stagingPaymentRepository.findById(receipt.getOrderId());

        if (stagingPaymentOptional.isPresent()){
            new ModelMapper().map(stagingPaymentOptional.get(), mapped);
            mapped.setReservationStatus(ReservationStatus.COMPLETED);
            Receipt saved = receiptRepository.save(mapped);
            stagingPaymentRepository.delete(stagingPaymentOptional.get());

            return saved.getOrderId() != null && saved.getOrderId().equals(receipt.getOrderId());
        }

        return false;
    }

    @Transactional
    public int deleteStagingPayment(ReceiptTO receipt) {
        int statusCode = 400;
        Optional<StagingPayment> stagingPaymentOptional = stagingPaymentRepository.findById(receipt.getOrderId());

        if (stagingPaymentOptional.isPresent()) {
            StagingPayment stagingPayment = stagingPaymentOptional.get();

            stagingPaymentRepository.delete(stagingPayment);
            statusCode = 200;
        }

        return statusCode;
    }

    public List<ReceiptInfoTO> getReceiptsByPlaceSeq(Long placeSeq) {
        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> receiptJPAQuery = new JPAQuery<>(em);

        QRentalPlace qRentalPlace = QRentalPlace.rentalPlace;
        QUser qUser = QUser.user;

        List<ReceiptInfoTO> receiptInfoList = new ArrayList<>();

        receiptJPAQuery.select(qReceipt)
                .from(qReceipt)
                .where(qReceipt.rentalPlaceSeq.eq(placeSeq).and(
                        qReceipt.reservationStatus.eq(ReservationStatus.COMPLETED)
                ))
                .orderBy(qReceipt.reservedAt.desc())
                .fetch()
                .forEach(item -> {
                    ReceiptInfoTO receiptInfoTO = new ModelMapper().map(item, ReceiptInfoTO.class);

                    JPAQuery<RentalPlace> rentalPlaceJPAQuery = new JPAQuery<>(em);
                    JPAQuery<User> userJPAQuery = new JPAQuery<>(em);

                    receiptInfoTO.setRentalPlaceName(
                            rentalPlaceJPAQuery.select(qRentalPlace.name)
                                    .from(qRentalPlace)
                                    .where(qRentalPlace.id.eq(item.getRentalPlaceSeq()))
                                    .fetchOne()
                    );

                    receiptInfoTO.setReservationUserName(
                            userJPAQuery.select(qUser.name)
                                    .from(qUser)
                                    .where(qUser.id.eq(item.getPopupUserSeq()))
                                    .fetchOne()
                    );

                    receiptInfoTO.setPrice(
                            receiptInfoTO.getTotalAmount().divide(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(receiptInfoTO.getStartDate(), receiptInfoTO.getEndDate()) + 1
                                    ),0, RoundingMode.UP
                            )
                    );
                    receiptInfoTO.setReservationStatus(item.getReservationStatus().getDesc());

                    receiptInfoList.add(receiptInfoTO);
                });
        return receiptInfoList;
    }

    public List<ReceiptInfoTO> getReceiptsByUserSeq(Long userSeq) {
        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> receiptJPAQuery = new JPAQuery<>(em);

        QRentalPlace qRentalPlace = QRentalPlace.rentalPlace;

        List<ReceiptInfoTO> receiptInfoList = new ArrayList<>();

        receiptJPAQuery.select(qReceipt)
                .from(qReceipt)
                .where(qReceipt.popupUserSeq.eq(userSeq))
                .orderBy(qReceipt.reservedAt.desc())
                .fetch()
                .forEach(item -> {
                    ReceiptInfoTO receiptInfoTO = new ReceiptInfoTO();
                    new ModelMapper().map(item, receiptInfoTO);
                    JPAQuery<RentalPlace> rentalPlaceJPAQuery = new JPAQuery<>(em);

                    receiptInfoTO.setRentalPlaceName(
                            rentalPlaceJPAQuery.select(qRentalPlace.name)
                                    .from(qRentalPlace)
                                    .where(qRentalPlace.id.eq(item.getRentalPlaceSeq()))
                                    .fetchOne()
                    );
                    receiptInfoTO.setPrice(
                        receiptInfoTO.getTotalAmount().divide(
                            BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(receiptInfoTO.getStartDate(), receiptInfoTO.getEndDate()) + 1
                            ),0, RoundingMode.UP
                        )
                    );
                    receiptInfoTO.setReservationStatus(item.getReservationStatus().getDesc());

                    receiptInfoList.add(receiptInfoTO);
                });

        return receiptInfoList;
    }

    @Transactional
    public TossPaymentTO changeReservationStatus(Long userId, String orderId) {
        QReceipt qReceipt = QReceipt.receipt;
        JPAUpdateClause jpaUpdateClause = new JPAUpdateClause(em, qReceipt);
        JPAQuery<Receipt> query = new JPAQuery<>(em);
        Receipt receipt = query.select(qReceipt).from(qReceipt)
                        .where(qReceipt.orderId.eq(orderId)).fetchOne();

        jpaUpdateClause.set(qReceipt.reservationStatus, ReservationStatus.CANCELED)
                .where(qReceipt.orderId.eq(orderId)
                        .and(qReceipt.popupUserSeq.eq(userId))
                        .and(qReceipt.reservationStatus.eq(ReservationStatus.COMPLETED)))
                .execute();

        if (receipt != null) {
            return new ModelMapper().map(receipt, TossPaymentTO.class);
        } else {
            return null;
        }
    }

    public List<RangeDateTO> getRangeDates(Long rentalPlaceSeq) {
        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> query = new JPAQuery<>(em);

        List<RangeDateTO> rangeDates = new ArrayList<>();

        query.select(qReceipt).from(qReceipt)
                .where(qReceipt.rentalPlaceSeq.eq(rentalPlaceSeq).and(
                        qReceipt.reservationStatus.eq(ReservationStatus.COMPLETED)
                ).and(
                        qReceipt.startDate.gt(LocalDate.now())
                )).fetch().forEach(item -> {
                    RangeDateTO rangeDateTO = new RangeDateTO();
                    rangeDateTO.setStartDate(item.getStartDate());
                    rangeDateTO.setEndDate(item.getEndDate());
                    rangeDates.add(rangeDateTO);
                });

        return rangeDates;
    }
}