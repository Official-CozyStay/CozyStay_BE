package com.project.cozystay.payment.external.toss;

import com.project.cozystay.payment.config.TossPaymentProperties;
import com.project.cozystay.payment.exception.TossPaymentConfirmException;
import com.project.cozystay.payment.external.toss.dto.TossConfirmRequest;
import com.project.cozystay.payment.external.toss.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {
// 외부 결제사(Toss)와 직접 통신하는 전용 클라이언트

    private final TossPaymentProperties tossPaymentProperties;
    // 외부 HTTP API 호출할 때 쓰는 스프링 클래스
    private final RestTemplate restTemplate = new RestTemplate();

    // 결제 승인 요청 보냄
    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, BigDecimal amount) {

        // Basic Authorization용 인코딩
        // Authorization: Basic bas64(secretKye:)
        String secretKey = tossPaymentProperties.getSecretKey();

        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        // 헤더 생성
        // Content-Type : application/json
        // Authorization: Basic...
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedKey);

        // 요청 DTO 생성
        TossConfirmRequest request = new TossConfirmRequest(paymentKey, orderId, amount);

        // HttpEntity로 헤더 + 바디 묶기
        HttpEntity<TossConfirmRequest> entity = new HttpEntity<>(request, headers);

        try{
            // 실제 외부 API 호출
            ResponseEntity<TossConfirmResponse> response = restTemplate.exchange(
                    tossPaymentProperties.getConfirmUrl(),
                    HttpMethod.POST,
                    entity,
                    TossConfirmResponse.class
            );

            // 토스 서버가 준 응답 바디 반환
            return response.getBody();
        }catch (RestClientException e){
            throw new TossPaymentConfirmException("토스 결제 승인 API 호출에 실패했습니다.", e);
        }




    }
}
