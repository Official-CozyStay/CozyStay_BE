package com.project.cozystay.booking.guest.service;

public interface InvitationEmailSender {
    void send(String toEmail, String guestName, String acceptLink, String declineLink);
}
