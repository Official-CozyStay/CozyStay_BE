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
    public void send(String toEmail, String guestName, String invitationPageLink){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("[CozyStay] 예약 초대가 도착했습니다.");

        String body = """
                    안녕하세요. %s 님
                    
                    CozyStay 예약 초대가 도착했습니다. 
                    
                    초대에 응답하려면 아래 링크를 클릭하세요:
                    
                    %s
                    
                    링크를 통해 초대에 대해 수락/거절을 선택해주세요.
                    (링크는 만료될 수 있습니다.)
                   
                    """.formatted(guestName.replaceAll("[\\r\\n]", " "), invitationPageLink);

        message.setText(body);
        mailSender.send(message);


    }
}

