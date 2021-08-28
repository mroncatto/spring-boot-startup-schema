package com.mroncatto.startup.service.impl;

import com.mroncatto.startup.constant.AppConstant;
import com.mroncatto.startup.service.EmailService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mroncatto.startup.constant.AppConstant.*;

@Service
public class EmailServiceImpl implements EmailService {

    private final Configuration configuration;
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(Configuration configuration, JavaMailSender javaMailSender) {
        this.configuration = configuration;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String recipientEmail, String recipientName, String subject, String message) throws MessagingException, IOException, TemplateException {
        InternetAddress recipient = new InternetAddress();
        recipient.setPersonal(recipientName,"UTF-8");
        recipient.setAddress(recipientEmail);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(subject);
        helper.setTo(recipient);
        helper.setSentDate(new Date());
        helper.setFrom(APP_SENDER_EMAIL);
        String emailContent = getEmailTemplate(message);
        javaMailSender.send(mimeMessage);
    }

    // Render Email Template
    private String getEmailTemplate(String message) throws IOException, TemplateException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("app_name", APP_NAME);
        model.put("app_owner", APP_OWNER);
        model.put("message", message);
        model.put("date",getFormatDate(APP_FORMAT_DATE_FULL));
        configuration.getTemplate("email.ftlh").process(model, stringWriter);
        return stringWriter.getBuffer().toString();
    }

    // Format date by pattern
    private String getFormatDate(String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }


}
