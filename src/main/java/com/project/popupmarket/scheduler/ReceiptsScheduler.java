package com.project.popupmarket.scheduler;

import com.project.popupmarket.entity.QReceipt;
import com.project.popupmarket.entity.QStagingPayment;
import com.project.popupmarket.entity.Receipt;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceiptsScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReceiptsScheduler.class);
    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void rentalPlaceDailyChangeStatus() {
        QReceipt qReceipt = QReceipt.receipt;
        JPAQuery<Receipt> query = new JPAQuery<>(em);

        LocalDate today = LocalDate.now();

        List<Receipt> receipts = query.select(qReceipt).from(qReceipt)
                .where(qReceipt.startDate.goe(today)
                        .and(qReceipt.reservationStatus.eq(Receipt.ReservationStatus.COMPLETED)))
                .fetch();

        receipts.forEach(receipt -> {
            receipt.setReservationStatus(Receipt.ReservationStatus.LEASED);
        });
        em.flush();
        em.clear();
        
        log.info("Receipt 예약 상태 변경 : {}", receipts.size());
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void StagingPaymentDeleteDummyData() {
        QStagingPayment qStagingPayment = QStagingPayment.stagingPayment;
        JPADeleteClause deleteClause = new JPADeleteClause(em, qStagingPayment);
        LocalDateTime today = LocalDate.now().atStartOfDay();

        long deletedCount = deleteClause.where(qStagingPayment.updatedAt.lt(today)).execute();

        log.info("StagingPayment 더미 데이터 삭제 : {}", deletedCount);
    }

}
