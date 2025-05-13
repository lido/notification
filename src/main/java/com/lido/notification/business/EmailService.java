package com.lido.notification.business;

import com.lido.notification.business.dto.TasksDTO;
import com.lido.notification.infrastructure.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${send.email.sender}")
    public String sender;

    @Value("${send.email.nameSender}")
    public String nameSender;

    public void sendEmail(TasksDTO dto){

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true,
                    StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(new InternetAddress(sender, nameSender));
            mimeMessageHelper.setTo(InternetAddress.parse(dto.getUserEmail()));
            mimeMessageHelper.setSubject("Notification Task");

            Context context = new Context();
            context.setVariable("nameTask",dto.getNameTask());
            context.setVariable("dateOfEvent",dto.getDateOfEvent());
            context.setVariable("description",dto.getDescription());
            String template = templateEngine.process("notification", context);
            mimeMessageHelper.setText(template, true);
            javaMailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException("Error sender email "+e.getCause());
        }

    }
}
