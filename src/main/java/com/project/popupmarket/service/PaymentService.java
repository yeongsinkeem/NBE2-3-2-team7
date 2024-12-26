package com.project.popupmarket.service;

import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.dto.response.RespObjectTO;
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
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.project.popupmarket.entity.Receipt.ReservationStatus;
import static com.project.popupmarket.entity.Receipt.Status;

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

    public RespObjectTO<ReservationInfoTO> getPaymentInfo(ReservationTO reservation) {
        boolean flag = receiptReservationDateCheck(reservation);

        ReservationInfoTO reservationInfoTO = new ReservationInfoTO();
        if (flag) {
            // 데이터 조회 == null -> 유저 및 임대지 조회
            // 사용자 및 임대지 정보 조회 By 번호, 로직 추가 필요
            // 임시 데이터로 진행
            reservationInfoTO.setCustomerKey(UUID.randomUUID().toString());
            reservationInfoTO.setPlaceName("임대지 이름1");
            reservationInfoTO.setPrice(BigDecimal.valueOf(100000));
            reservationInfoTO.setUserEmail("admin@test.com");
            reservationInfoTO.setUserName("홍길동");
            reservationInfoTO.setUserTel("010-1234-5678");
            reservationInfoTO.setArea("12345");
            reservationInfoTO.setAddress("인천광역시 연수구 송도문화로 12");
            reservationInfoTO.setAddrDetail("돌돌 아파트 202동 303호");

            return new RespObjectTO<>(200, "예약 및 결제 가능", reservationInfoTO);
        } else {
            // 데이터 조회 != null -> 메세지 반환, 이미 예약된 날짜입니다.
            return new RespObjectTO<>(400, "이미 예약된 날짜입니다.", null);
        }
    }

    @Transactional
    public int insertStagingPayment(ReceiptTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        ModelMapper modelMapper = new ModelMapper();
        StagingPayment stagingPayment = modelMapper.map(receipt, StagingPayment.class);
        System.out.println(receipt);
        System.out.println(stagingPayment);
        StagingPayment saved = stagingPaymentRepository.save(stagingPayment);

        return (saved.getOrderId() != null && saved.getOrderId().equals(stagingPayment.getOrderId())) ? 200 : 400;
    }

    @Transactional
    public int insertReceipt(ReceiptTO receipt) {
        // 유저랑 임대지 테이블 병합시 수정 필요.

        ModelMapper modelMapper = new ModelMapper();
        Receipt mapped = modelMapper.map(receipt, Receipt.class);

        int statusCode = 400;

        Optional<StagingPayment> stagingPaymentOptional = stagingPaymentRepository.findById(receipt.getOrderId());

        if (stagingPaymentOptional.isPresent()){
            StagingPayment stagingPayment = stagingPaymentOptional.get();
            modelMapper.map(stagingPayment, mapped);
            mapped.setReservationStatus(ReservationStatus.COMPLETED);
            mapped.setStatus(Status.ACTIVE);
            receiptRepository.save(mapped);
            stagingPaymentRepository.delete(stagingPayment);

            statusCode = 200;
        }

        return statusCode;
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

    public RespObjectTO<List<ReceiptInfoTO>> getReceipts(Long userId) {
        ModelMapper modelMapper = new ModelMapper();

        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> receiptJPAQuery = new JPAQuery<>(em);

        QRentalPlace qRentalPlace = QRentalPlace.rentalPlace;

        List<ReceiptInfoTO> receiptInfoList = new ArrayList<>();

        receiptJPAQuery.select(qReceipt)
                .from(qReceipt)
                .where(qReceipt.popupUserSeq.eq(userId))
                .orderBy(qReceipt.reservedAt.desc())
                .fetch()
                .forEach(item -> {
                    ReceiptInfoTO receiptInfoTO = new ReceiptInfoTO();
                    modelMapper.map(item, receiptInfoTO);
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

        return new RespObjectTO<>(200, "success", receiptInfoList);
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
            ModelMapper modelMapper = new ModelMapper();

            return modelMapper.map(receipt, TossPaymentTO.class);
        }
        return null;
    }
}
