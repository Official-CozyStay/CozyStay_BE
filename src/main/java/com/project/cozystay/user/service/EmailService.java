package com.project.cozystay.user.service;
import com.project.cozystay.user.exception.UserEmailAlreadyExistsException;
import com.project.cozystay.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    // TODO 추후 Map<> 은  Redis 등으로 변경 고려
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>(); // 인증코드 임시 저장소
    private final Map<String, Boolean> verifiedEmailMap = new ConcurrentHashMap<>(); // 인증된 이메일 저장소

    @Value("${cozystay.mail.from}")
    private String from;

    // 이메일 인증 코드 발송
    public void sendVerificationEmail(String email) {

        if (userRepository.existsByEmail(email)) {
            throw UserEmailAlreadyExistsException.of(email);
        }

        String code = createRandomCode();

        // 임시 저장소
        verificationStorage.put(email, code);

        // 인증된 이메일 기록 삭제
        verifiedEmailMap.remove(email);

        sendEmail(email, code);
    }

    // 이메일 인증 코드 비교하기
    public boolean verifyCode(String email, String code) {

        String savedCode = verificationStorage.get(email);

        // 코드가 없거나 다르면 거부
        if (savedCode == null || !savedCode.equals(code)) {
            return false;
        }

        // 일치하면 임시 저장소에서 제거
        verificationStorage.remove(email);

        // 인증된 이메일으로 기록
        verifiedEmailMap.put(email, true);

        return true;
    }

    // 회원가입 시 이메일 인증 여부 확인
    public boolean isVerified(String email) {

        boolean isVerified = verifiedEmailMap.getOrDefault(email, false);

        // 확인했으면 명단에서 제거
        if (isVerified) {
            verifiedEmailMap.remove(email);
        }
        return isVerified;
    }

    // ========== 헬퍼 메서드 ========

    // 메일 발송
    private void sendEmail(String email, String code) {
        String title = "[CozyStay] 이메일 인증 코드";
        String content = "인증 코드: <strong>" + code + "</strong>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);
            helper.setFrom(from);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }

    // 랜덤 숫자 생성
    private String createRandomCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
