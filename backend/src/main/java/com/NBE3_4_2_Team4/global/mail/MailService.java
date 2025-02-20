package com.NBE3_4_2_Team4.global.mail;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);  // true -> HTML 형식 지원

            mailSender.send(message);
            System.out.println("✅ 이메일 전송 완료!");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
