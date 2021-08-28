package com.mroncatto.startup.service;

import freemarker.template.TemplateException;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmail(String recipientEmail, String recipientName, String subject, String message) throws MessagingException,
            IOException, TemplateException;
}
