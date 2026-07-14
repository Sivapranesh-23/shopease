package com.shopease.service.impl;

import com.shopease.domain.Category;
import com.shopease.domain.Product;
import com.shopease.domain.User;
import com.shopease.domain.enums.OrderStatus;
import com.shopease.domain.enums.PaymentStatus;
import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.request.PlaceOrderRequest;
import com.shopease.dto.response.CartResponse;
import com.shopease.dto.response.OrderResponse;
import com.shopease.dto.response.PaymentIntentResponse;
import com.shopease.repository.CategoryRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.CartService;
import com.shopease.service.OrderService;
import com.shopease.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.shopease.repository.search.ProductSearchRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CheckoutIntegrationTest {

    @MockBean private ProductSearchRepository productSearchRepository;
    @MockBean private ElasticsearchOperations elasticsearchOperations;

    @Autowired UserRepository userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired CartService cartService;
    @Autowired OrderService orderService;
    @Autowired PaymentService paymentService;

    @Test
    void testOrderCheckoutLifecycle() {
        // 1. Create a Category, Product, and User
        Category category = categoryRepository.save(Category.builder()
                .name("Electronics")
                .slug("electronics")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("Wireless Headset")
                .slug("wireless-headset")
                .sku("HEADSET-123")
                .price(new BigDecimal("1200.00"))
                .stock(10)
                .category(category)
                .active(true)
                .build());

        User user = userRepository.save(User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encoded_pass")
                .active(true)
                .build());

        // 2. Add product to user's cart
        cartService.addItem(user.getId(), new AddToCartRequest(product.getId(), 2));
        CartResponse cartBefore = cartService.getCart(user.getId());
        assertThat(cartBefore.items()).hasSize(1);
        assertThat(cartBefore.items().get(0).quantity()).isEqualTo(2);

        // 3. Place a pending order
        PlaceOrderRequest.CartItemPayload itemPayload = new PlaceOrderRequest.CartItemPayload(product.getId(), 2);
        PlaceOrderRequest.AddressPayload addressPayload = new PlaceOrderRequest.AddressPayload(
                "John Doe", "1234567890", "Street 1", "", "City", "12345", "Country"
        );
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(addressPayload, null, List.of(itemPayload));

        OrderResponse orderResponse = orderService.placeOrder(user.getId(), placeOrderRequest);

        // Assert: Order status is PENDING
        assertThat(orderResponse.status()).isEqualTo(OrderStatus.PENDING);
        // Assert: Stock is reserved (reduced from 10 to 8)
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(8);
        // Assert: Cart is NOT cleared yet (still contains the items)
        CartResponse cartAfterPlacement = cartService.getCart(user.getId());
        assertThat(cartAfterPlacement.items()).isNotEmpty();

        // 4. Create payment intent and confirm payment
        PaymentIntentResponse intent = paymentService.createIntent(
                orderResponse.id(), orderResponse.grandTotal(), "INR"
        );
        
        PaymentStatus paymentStatus = paymentService.confirm(intent.providerPaymentId());

        // Assert: Payment succeeded
        assertThat(paymentStatus).isEqualTo(PaymentStatus.SUCCEEDED);
        // Assert: Order is now PAID
        OrderResponse paidOrder = orderService.getOrderForUser(user.getId(), orderResponse.orderNumber());
        assertThat(paidOrder.status()).isEqualTo(OrderStatus.PAID);
        // Assert: Cart is now cleared
        CartResponse cartAfterPayment = cartService.getCart(user.getId());
        assertThat(cartAfterPayment.items()).isEmpty();
    }
}
