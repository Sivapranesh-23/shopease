package com.shopease.service;

import com.shopease.domain.Order;

/**
 * Email / notification dispatch. Implementations may use Spring Mail, a third-party
 * API (SendGrid, Mailgun, etc.), or simply log the notification.
 */
public interface NotificationService {

    void sendOrderConfirmation(Order order);

    void sendShippingNotification(Order order);

    void sendPasswordResetEmail(String email, String resetToken);

    void sendCancellationNotification(Order order);
}
