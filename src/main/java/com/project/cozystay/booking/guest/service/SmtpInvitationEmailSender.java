package com.project.cozystay.booking.guest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cozystay.mail.enabled", havingValue = "true")
public class SmtpInvitationEmailSender implements InvitationEmailSender{

    private final JavaMailSender mailSender;

    @Value("${cozystay.mail.from}")
    private String from;

    @Override
    public void send(String toEmail, String guestName, String acceptLink, String declineLink){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("[CozyStay] 예약 초대가 도착했습니다.");

        String body = """
                    안녕하세요. %s 님
                    
                    CozyStay 예약 초대가 도착했습니다. 
                    
                    수락 : %s
                    거절 : %s
                    
                    링크를 통해 초대에 대해 수락/거절을 선택해주세요.
                    (링크는 만료될 수 있습니다.)
                   
                    """.formatted(guestName, acceptLink, declineLink);

        message.setText(body);
        mailSender.send(message);


    }
}

