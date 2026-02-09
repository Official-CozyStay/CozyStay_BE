package com.project.cozystay.booking.guest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "cozystay.mail.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class ConsoleInvitationEmailSender implements InvitationEmailSender {
    @Override
    public void send(String toEmail, String guestName, String acceptLink, String declineLink) {
        log.info("[INVITATION EMAIL - CONSOLE] to={}, name={}", toEmail, guestName);
        log.info("ACCEPT: {}", acceptLink);
        log.info("DECLINE: {}", declineLink);
    }
}
