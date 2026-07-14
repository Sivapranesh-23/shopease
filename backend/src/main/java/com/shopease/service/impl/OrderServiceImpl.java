package com.shopease.service.impl;

import com.shopease.domain.Order;
import com.shopease.domain.OrderItem;
import com.shopease.domain.Product;
import com.shopease.domain.User;
import com.shopease.domain.enums.OrderStatus;
import com.shopease.dto.request.PlaceOrderRequest;
import com.shopease.dto.response.OrderResponse;
import com.shopease.exception.InsufficientStockException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.OrderMapper;
import com.shopease.repository.OrderRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.NotificationService;
import com.shopease.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("2000.00");
    private static final BigDecimal FLAT_SHIPPING = new BigDecimal("149.00");
    private static final BigDecimal TAX_RATE = BigDecimal.ZERO;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final NotificationService notificationService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            OrderMapper orderMapper,
                            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.notificationService = notificationService;
    }

    @Override
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Resolve products and reserve stock in a single transaction.
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (PlaceOrderRequest.CartItemPayload line : request.items()) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", line.productId()));
            if (!product.isActive()) {
                throw new ResourceNotFoundException("Product is no longer available: " + product.getName());
            }
            if (product.getStock() < line.quantity()) {
                throw new InsufficientStockException(
                        "Only " + product.getStock() + " units of '" + product.getName() + "' are available");
            }
            // Decrement stock immediately.
            product.setStock(product.getStock() - line.quantity());
            productRepository.save(product);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(line.quantity()));
            subtotal = subtotal.add(lineTotal);

            items.add(OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .imageUrl(product.getImageUrl())
                    .quantity(line.quantity())
                    .unitPrice(product.getPrice())
                    .build());
        }

        BigDecimal shipping = subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0
                ? BigDecimal.ZERO : FLAT_SHIPPING;
        BigDecimal tax = subtotal.multiply(TAX_RATE);

        PlaceOrderRequest.AddressPayload addr = request.shippingAddress();
        String addressLine = String.join(", ",
                addr.fullName(), addr.line1(),
                addr.line2() == null ? "" : addr.line2(),
                addr.city(), addr.postalCode(), addr.country());

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .shippingTotal(shipping)
                .taxTotal(tax)
                .grandTotal(subtotal.add(shipping).add(tax))
                .shippingAddress(addressLine)
                .build();

        for (OrderItem item : items) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    @NonNull
    @Transactional(readOnly = true)
    public OrderResponse getOrderForUser(Long userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order", "orderNumber", orderNumber);
        }
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> myOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(orderMapper::toResponse);
    }

    // --- Admin ---

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> listAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> adminList(OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByStatus(status, pageable).map(orderMapper::toResponse);
        }
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable).map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long orderId) {
        return adminGet(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse adminGet(Long orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        return updateStatus(orderId, status);
    }

    @Override
    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        OrderStatus previous = order.getStatus();
        order.setStatus(status);
        order = orderRepository.save(order);

        if (status == OrderStatus.SHIPPED && previous != OrderStatus.SHIPPED) {
            notificationService.sendShippingNotification(order);
        }
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse assignTracking(Long orderId, String carrier, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setShippingCarrier(carrier);
        order.setTrackingNumber(trackingNumber);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        
        if (!CANCELLABLE.contains(order.getStatus())) {
            throw new IllegalStateException("Order cannot be cancelled in " + order.getStatus() + " status");
        }
        
        // Restore product stock
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        notificationService.sendCancellationNotification(order);
        return orderMapper.toResponse(order);
    }

    private static final Set<OrderStatus> CANCELLABLE =
            Set.of(OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.PROCESSING);

    private String generateOrderNumber() {
        int n = 1000 + RANDOM.nextInt(9000);
        return "LX-" + n;
    }
}
