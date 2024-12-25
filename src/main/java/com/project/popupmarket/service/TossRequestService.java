package com.project.popupmarket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.popupmarket.configure.TossPaymentConfig;
import com.project.popupmarket.dto.payment.TossPaymentTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TossRequestService {

    private final TossPaymentConfig tossPaymentConfig;

    private static final Logger log = LoggerFactory.getLogger(TossRequestService.class);

    @Autowired
    public TossRequestService(TossPaymentConfig tossPaymentConfig) {
        this.tossPaymentConfig = tossPaymentConfig;
    }

    public HttpResponse<String> requestPayment(TossPaymentTO payment) {
        String encodeKey = Base64.getEncoder().encodeToString((tossPaymentConfig.getTestSecreteKey()+":").getBytes(StandardCharsets.UTF_8));

        HttpResponse<String> response = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("paymentKey", payment.getPaymentKey());
            requestBody.put("amount", payment.getAmount());
            requestBody.put("orderId", payment.getOrderId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TossPaymentConfig.TOSS_URL + "confirm"))
                    .header("Authorization", "Basic " + encodeKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return response;
    }

    public HttpResponse<String> cancelPayment(String paymentKey, String message) {

        String encodeKey = Base64.getEncoder().encodeToString((tossPaymentConfig.getTestSecreteKey()+":").getBytes(StandardCharsets.UTF_8));

        HttpResponse<String> response = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TossPaymentConfig.TOSS_URL + paymentKey + "/cancel"))
                    .header("Authorization", "Basic "+encodeKey)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\""+ message +"\"}"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return response;
    }
}
