package com.NBE3_4_2_Team4.global.mail.service;


import com.NBE3_4_2_Team4.global.mail.state.MailState;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String body){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);  // true -> HTML 형식 지원

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Error sending email to {}, msg : {}", to, e.getMessage());
        }
    }

    private String makeThymeleafMailContent(String templateName, Map<String, String> variables) {
        Context context = new Context();
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return templateEngine.process(templateName, context);
    }

    public void sendAuthenticationMail(String email, Long memberId, String authCode){
        Map<String, String> variables = Map.of(
                "memberId", memberId.toString(),
                "authCode", authCode
        );
        String body = makeThymeleafMailContent("auth-email", variables);
        sendEmail(email, "인증 완료해주세용", body);
    }
}
