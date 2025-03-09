package com.NBE3_4_2_Team4.global.mail.service

import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

open class MailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine
) {
    @Value("\${custom.domain.backend}")
    lateinit var backendDomain: String

    private val log = LoggerFactory.getLogger(MailService::class.java)


    private fun sendEmail(to: String, subject: String, content: String) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(to)
            helper.setSubject(subject)
            helper.setText(content, true)
            mailSender.send(message)
        }catch (e: MessagingException){
            log.error("Error sending email to {}, msg : {}", to, e.message);
        }
    }

    private fun makeThymeleafMailContent(templateName: String, variables: Map<String, String>): String{
        val context = Context()
        for ((key, value) in variables){
            context.setVariable(key, value)
        }
        return templateEngine.process(templateName, context)
    }

    @Async
    open fun sendAuthenticationMail(emailAddress: String, memberId: Long, authCode: String){
        val variables = java.util.Map.of(
            "backendDomain", backendDomain,
            "memberId", memberId.toString(),
            "authCode", authCode
        )
        val content = makeThymeleafMailContent("auth-email", variables)
        sendEmail(emailAddress, "[WikiPoint] 이메일 인증을 완료해주세요.", content)
    }

}