package com.project.popupmarket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.popupmarket.dto.payment.*;
import com.project.popupmarket.dto.response.RespTO;
import com.project.popupmarket.service.PaymentService;
import com.project.popupmarket.service.TossRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final TossRequestService tossRequestService;


    @Autowired
    public PaymentController(PaymentService paymentService, TossRequestService tossRequestService) {
        this.paymentService = paymentService;
        this.tossRequestService = tossRequestService;
    }

    @GetMapping("/payment")
    public ResponseEntity<RespTO<ReservationInfoTO>> reservationInfo(
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

        RespTO<ReservationInfoTO> resp = paymentService.getPaymentInfo(reservation);

        if (resp.getStatus() == 200) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(resp.getStatus()).body(resp);
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<RespTO<Object>> payment(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestBody ReceiptTO receipt
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        reservation.setUserSeq(userId);
        receipt.setPopupUserSeq(1L);

        int code = paymentService.insertStagingPayment(receipt);

        if (code == 200) {
            return ResponseEntity.ok(new RespTO<>(code, "success", null));
        } else {
            return ResponseEntity.status(code).body(new RespTO<>(code, "fail", null));
        }
    }

    @PostMapping("/payment/success")
    public ResponseEntity<RespTO<?>> paymentSuccess(
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

            int success = paymentService.insertReceipt(receipt);

            if (success == 200) {
                return ResponseEntity.ok(new RespTO<>(success,"결제 성공", response.body()));
            } else {
                HttpResponse<String> canceled = tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
                return ResponseEntity.status(success).body(new RespTO<>(success, "결제 실패", canceled.body()));
            }
        } else if (response != null){
            // 실패 응답 반환
            return ResponseEntity.status(response.statusCode())
                    .body(new RespTO<>(response.statusCode(),"결제 실패", response.body()));
        } else {
            return ResponseEntity.status(500)
                    .body(new RespTO<>(500,"시스템 에러", null));
        }
    }

    @DeleteMapping("/payment/fail")
    public ResponseEntity<RespTO<Object>> paymentFail(
//            @RequestHeader("Authorization") String token, 추후 수정 예정
            @RequestBody ReceiptTO receipt
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
//        reservation.setUserSeq(userId);

        int statusCode = paymentService.deleteStagingPayment(receipt);
        if (statusCode == 200) {
            return ResponseEntity.ok(new RespTO<>(statusCode, "success", null));
        } else {
            return ResponseEntity.status(statusCode).body(new RespTO<>(statusCode, "fail", null));
        }
    }

    @GetMapping("/receipt")
    public ResponseEntity<RespTO<List<ReceiptInfoTO>>> receipt(/*@RequestHeader("Authorization") String token*/) {
        Long userId = 1L;

        return ResponseEntity.ok(paymentService.getReceipts(userId));
    }

    @PutMapping("/receipt/{orderId}")
    public ResponseEntity<RespTO<Object>> receipt(
            /*@RequestHeader("Authorization") String token*/
            @PathVariable String orderId
    ) {
//        Long userId = Long.parseLong(jwtTokenProvider.extractUserId(token.replace("Bearer ", "")));
        Long userId = 1L;


        TossPaymentTO payment = paymentService.changeReservationStatus(userId, orderId);
        if (payment != null) {
            HttpResponse<String> canceled = tossRequestService.cancelPayment(payment.getPaymentKey(), "시스템 에러로 인한 결제 취소");
            return ResponseEntity.ok(new RespTO<>(canceled.statusCode(), "success", canceled.body()));
        } else {
            return ResponseEntity.status(500).body(new RespTO<>(500, "fail", null));
        }
    }
}
