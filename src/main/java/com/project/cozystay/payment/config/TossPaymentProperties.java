package com.project.cozystay.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "toss")
public class TossPaymentProperties {
    private String clientKey;
    private String secretKey;
    private String confirmUrl;
}
