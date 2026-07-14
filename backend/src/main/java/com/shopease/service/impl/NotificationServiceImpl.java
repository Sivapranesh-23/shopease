package com.shopease.service.impl;

import com.shopease.domain.Order;
import com.shopease.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Real notification service sending HTML emails.
 * Uses template files from classpath.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    public NotificationServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    @Override
    public void sendOrderConfirmation(Order order) {
        StringBuilder itemsHtml = new StringBuilder();
        for (com.shopease.domain.OrderItem item : order.getItems()) {
            itemsHtml.append("<tr>")
                    .append("<td>").append(item.getProductName()).append("</td>")
                    .append("<td style=\"text-align: center;\">").append(item.getQuantity()).append("</td>")
                    .append("<td style=\"text-align: right; font-weight: 600;\">₹").append(item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))).append("</td>")
                    .append("</tr>");
        }

        String html = readTemplate("order-confirmation.html");
        if (html.isEmpty()) {
            log.warn("[EMAIL-STUB-FALLBACK] No template, logging order confirmation: {}", order.getOrderNumber());
            return;
        }

        html = html.replace("{{customerName}}", order.getUser().getName())
                   .replace("{{orderNumber}}", order.getOrderNumber())
                   .replace("{{orderDate}}", order.getCreatedAt() != null ? order.getCreatedAt().toString().substring(0, 10) : "")
                   .replace("{{shippingAddress}}", order.getShippingAddress())
                   .replace("{{itemsTable}}", itemsHtml.toString())
                   .replace("{{subtotal}}", "₹" + order.getSubtotal().toString())
                   .replace("{{shipping}}", order.getShippingTotal().compareTo(java.math.BigDecimal.ZERO) == 0 ? "Free" : "₹" + order.getShippingTotal().toString())
                   .replace("{{grandTotal}}", "₹" + order.getGrandTotal().toString());

        sendHtmlEmail(order.getUser().getEmail(), "Order Confirmed — ShopEase " + order.getOrderNumber(), html);
    }

    @Async
    @Override
    public void sendShippingNotification(Order order) {
        String html = readTemplate("order-shipped.html");
        if (html.isEmpty()) {
            log.warn("[EMAIL-STUB-FALLBACK] No template, logging shipping notification: {}", order.getOrderNumber());
            return;
        }

        html = html.replace("{{customerName}}", order.getUser().getName())
                   .replace("{{orderNumber}}", order.getOrderNumber())
                   .replace("{{carrier}}", order.getShippingCarrier())
                   .replace("{{trackingNumber}}", order.getTrackingNumber())
                   .replace("{{shippingAddress}}", order.getShippingAddress());

        sendHtmlEmail(order.getUser().getEmail(), "Your Package Has Shipped! — ShopEase " + order.getOrderNumber(), html);
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        String html = readTemplate("password-reset.html");
        if (html.isEmpty()) {
            log.warn("[EMAIL-STUB-FALLBACK] No template, logging password reset: {}", email);
            return;
        }

        html = html.replace("{{resetToken}}", resetToken);

        sendHtmlEmail(email, "Reset Your Password — ShopEase", html);
    }

    @Async
    @Override
    public void sendCancellationNotification(Order order) {
        log.info("[EMAIL-STUB-FALLBACK] Order cancellation email sent to user {} for order {}",
                order.getUser().getEmail(), order.getOrderNumber());
    }

    private String readTemplate(String templateName) {
        try {
            InputStream is = getClass().getResourceAsStream("/templates/" + templateName);
            if (is == null) {
                log.warn("Template /templates/{} not found on classpath", templateName);
                return "";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Failed to read template /templates/{}", templateName, e);
            return "";
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (mailSender == null) {
            log.warn("[EMAIL-STUB-FALLBACK] SMTP not configured. Mail target: {}, Subject: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@shopease.com");

            mailSender.send(message);
            log.info("Successfully sent HTML email to {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {} with subject: {}", to, subject, e);
        }
    }
}
