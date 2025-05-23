package com.techbytedev.warehousemanagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCodeEmail(String to, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Verification Code for SignBoard");
        helper.setText("Your verification code is: <strong>" + code + "</strong><br>This code will expire in 15 minutes.", true);

        mailSender.send(message);
    }

    public void sendLowStockAlert(String[] emails, String productName, int currentStock) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emails);
        message.setSubject("Cảnh báo tồn kho thấp: " + productName);
        message.setText("Sản phẩm " + productName + " chỉ còn " + currentStock + " đơn vị trong kho. Vui lòng kiểm tra!");

        mailSender.send(message);
    }

    // Phương thức gửi báo cáo kiểm kê qua email với file Excel đính kèm
    public void sendMonthlyStockCheckReport(String[] adminEmails, ByteArrayInputStream excelFile) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);
        helper.setTo(adminEmails);
        helper.setSubject("Báo cáo kiểm kê hàng tháng");
        helper.setText("Kính gửi Quản trị viên,\n\n" +
                "Đính kèm là báo cáo kiểm kê hàng tháng. Vui lòng kiểm tra.\n\n" +
                "Trân trọng,\nHệ thống quản lý kho");

        // Đính kèm file Excel
        helper.addAttachment("MonthlyStockCheckReport.xlsx", new ByteArrayResource(excelFile.readAllBytes()));

        mailSender.send(message);
    }
}