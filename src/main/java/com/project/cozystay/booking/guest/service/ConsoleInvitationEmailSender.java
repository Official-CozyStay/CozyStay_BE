package com.project.cozystay.booking.guest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsoleInvitationEmailSender implements InvitationEmailSender {
    @Override
    public void send(String toEmail, String guestName, String acceptLink, String declineLink) {
        log.info("[INVITATION EMAIL] to={}, name={}, acceptLink={}, declineLink={}",
                toEmail, guestName, acceptLink, declineLink);    }
}
