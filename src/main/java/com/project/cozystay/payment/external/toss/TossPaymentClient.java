package com.project.cozystay.payment.external.toss;

import com.project.cozystay.payment.config.TossPaymentProperties;
import com.project.cozystay.payment.external.toss.dto.TossConfirmRequest;
import com.project.cozystay.payment.external.toss.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final TossPaymentProperties tossPaymentProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, BigDecimal amount) {

        String secretKey = tossPaymentProperties.getSecretKey();

        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedKey);

        TossConfirmRequest request = new TossConfirmRequest(paymentKey, orderId, amount);

        HttpEntity<TossConfirmRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TossConfirmResponse> response = restTemplate.exchange(
                tossPaymentProperties.getConfirmUrl(),
                HttpMethod.POST,
                entity,
                TossConfirmResponse.class
        );

        return response.getBody();


    }
}
