package com.shopease.service.impl;

import com.shopease.domain.Order;
import com.shopease.domain.Payment;
import com.shopease.domain.enums.OrderStatus;
import com.shopease.domain.enums.PaymentStatus;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopease.service.CartService;
import com.shopease.service.NotificationService;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock PaymentRepository paymentRepository;
    @Mock OrderRepository orderRepository;
    @Mock CartService cartService;
    @Mock NotificationService notificationService;
    @InjectMocks PaymentServiceImpl paymentService;

    @Test
    void confirmationMarksBothPaymentAndOrderAsPaid() {
        com.shopease.domain.User user = com.shopease.domain.User.builder().id(1L).email("test@example.com").build();
        Order order = Order.builder().status(OrderStatus.PENDING).user(user).build();
        Payment payment = Payment.builder()
                .order(order)
                .providerPaymentId("pay_stub_123")
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentRepository.findByProviderPaymentId("pay_stub_123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentStatus status = paymentService.confirm("pay_stub_123");

        assertThat(status).isEqualTo(PaymentStatus.SUCCEEDED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).save(order);
    }
}
