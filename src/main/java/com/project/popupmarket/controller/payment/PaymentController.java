package com.project.popupmarket.controller.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.service.receipts.PaymentService;
import com.project.popupmarket.service.receipts.TossRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossRequestService tossRequestService;

    @Autowired
    public PaymentController(PaymentService paymentService, TossRequestService tossRequestService) {
        this.paymentService = paymentService;
        this.tossRequestService = tossRequestService;
    }

    @GetMapping("/payment")
    public ResponseEntity<ReservationInfoTO> reservationInfo(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestParam Long seq,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end
    ) {

        // 사용자 번호는 토큰을 통해 서버에서 받아온다.
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        reservation.setUserSeq(userId);
        ReservationTO reservation = new ReservationTO();

        reservation.setUserSeq(1L);
        reservation.setRentalPlaceSeq(seq);
        reservation.setStartDate(start);
        reservation.setEndDate(end);

        ReservationInfoTO resp = paymentService.getPaymentInfo(reservation);

        if (resp != null) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<String> payment(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestBody ReceiptTO receipt
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        reservation.setUserSeq(userId);
        receipt.setPopupUserSeq(1L);

        boolean flag = paymentService.insertStagingPayment(receipt);

        if (flag) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(400).body("fail");
        }
    }

    @PostMapping("/payment/success")
    public ResponseEntity<String> paymentSuccess(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestBody TossPaymentTO payment
    ) throws JsonProcessingException {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        .setUserSeq(userId);

        HttpResponse<String> response = tossRequestService.requestPayment(payment);

        ObjectMapper objectMapper = new ObjectMapper();

        if (response != null && response.statusCode() == 200) {
            ReceiptTO receipt = objectMapper.readValue(response.body(), ReceiptTO.class);
            receipt.setPopupUserSeq(1L);

            boolean flag = paymentService.insertReceipt(receipt);

            if (flag) {
                return ResponseEntity.ok("결제 성공");
            } else {
                HttpResponse<String> canceled = tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
                return ResponseEntity.status(500).body("결제 실패");
            }
        } else if (response != null){
            return ResponseEntity.status(response.statusCode())
                    .body("결제 실패");
        } else {
            return ResponseEntity.status(500)
                    .body("시스템 에러");
        }
    }

    @DeleteMapping("/payment/fail")
    public ResponseEntity<String> paymentFail(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestBody ReceiptTO receipt
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        reservation.setUserSeq(userId);

        int statusCode = paymentService.deleteStagingPayment(receipt);
        if (statusCode == 200) {
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(statusCode).body("fail");
        }
    }

    @GetMapping("/receipt")
    public ResponseEntity<List<ReceiptInfoTO>> receipt(/*@RequestHeader("Authorization") String token*/) {
        Long userId = 1L;

        List<ReceiptInfoTO> receipts = paymentService.getReceiptsByUserSeq(userId);

        if (!receipts.isEmpty()) {
            return ResponseEntity.ok(receipts);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reservation/{rentalPlaceSeq}")
    public ResponseEntity<List<ReceiptInfoTO>> reservation(@PathVariable Long rentalPlaceSeq) {
        List<ReceiptInfoTO> receipts = paymentService.getReceiptsByPlaceSeq(rentalPlaceSeq);

        if (!receipts.isEmpty()) {
            return ResponseEntity.ok(receipts);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/receipt/{orderId}")
    public ResponseEntity<String> receipt(
            /*@RequestHeader("Authorization") String token*/
            @PathVariable String orderId
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
        Long userId = 1L;

        TossPaymentTO payment = paymentService.changeReservationStatus(userId, orderId);

        if (payment != null) {
            HttpResponse<String> canceled = tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
            return ResponseEntity.ok("success");
        } else {
            return ResponseEntity.status(500).body("fail");
        }
    }

    // 임대지 기능과 병합시 삭제
    @GetMapping("/test/date")
    public ResponseEntity<List<RangeDateTO>> getRangeDates (@RequestParam Long placeSeq) {

        List<RangeDateTO> rangeDates = paymentService.getRangeDates(placeSeq);

        if (!rangeDates.isEmpty()) {
            return ResponseEntity.ok(rangeDates);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
