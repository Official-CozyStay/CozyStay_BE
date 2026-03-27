package com.project.cozystay.payment.domain;

public enum PaymentStatus {
    READY, // 결제 생성(결제 시도 전/중)
    SUCCESS, // 결제 승인 완료
    FAILED, // 결제 실패
    CANCELLED // 환불(취소)
}
