package com.shopease.service.impl;

import com.shopease.domain.Cart;
import com.shopease.domain.CartItem;
import com.shopease.domain.Product;
import com.shopease.domain.User;
import com.shopease.dto.request.AddToCartRequest;
import com.shopease.dto.response.CartResponse;
import com.shopease.exception.InsufficientStockException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.CartMapper;
import com.shopease.repository.CartRepository;
import com.shopease.repository.ProductRepository;
import com.shopease.repository.UserRepository;
import com.shopease.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return cartMapper.toResponse(getOrCreateCart(userId));
    }

    @Override
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.productId()));

        int requestedTotal = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.productId()))
                .mapToInt(i -> i.getQuantity())
                .findFirst()
                .orElse(0) + request.quantity();
        if (requestedTotal > product.getStock()) {
            throw new InsufficientStockException(
                    "Only " + product.getStock() + " units of '" + product.getName() + "' are available");
        }

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(0)
                            .unitPrice(product.getPrice())
                            .build();
                    cart.getItems().add(newItem);
                    return newItem;
                });
        item.setQuantity(item.getQuantity() + request.quantity());
        item.setUnitPrice(product.getPrice());

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse updateItem(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", "productId", productId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            Product product = item.getProduct();
            if (quantity > product.getStock()) {
                throw new InsufficientStockException(
                        "Only " + product.getStock() + " units of '" + product.getName() + "' are available");
            }
            item.setQuantity(quantity);
            item.setUnitPrice(product.getPrice());
        }
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart getOrCreateCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }
}
