package com.empmall.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 重點：加上 @Async
     * 這代表這個方法會被丟到「背景執行」，主程式不會等它跑完，會直接繼續往下走。
     */
    @Async
    public void sendAsyncMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message); // 這裡會花 5-10 秒，但現在沒差了，因為是在背景跑
            System.out.println("【背景執行】郵件已發送至：" + to);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("【背景執行】郵件發送失敗：" + e.getMessage());
        }
    }
}