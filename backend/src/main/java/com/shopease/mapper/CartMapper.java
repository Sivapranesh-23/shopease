package com.shopease.mapper;

import com.shopease.domain.Cart;
import com.shopease.domain.CartItem;
import com.shopease.dto.response.CartItemResponse;
import com.shopease.dto.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = BigDecimal.class)
public interface CartMapper {

    @Mapping(target = "productName",  source = "product.name")
    @Mapping(target = "productId",    source = "product.id")
    @Mapping(target = "productSlug",  source = "product.slug")
    @Mapping(target = "imageUrl",     source = "product.imageUrl")
    @Mapping(target = "lineTotal",    expression = "java(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    CartItemResponse toResponse(CartItem item);

    List<CartItemResponse> toItemResponseList(List<CartItem> items);

    default CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }
        var itemDtos = toItemResponseList(cart.getItems());
        BigDecimal subtotal = itemDtos.stream()
                .filter(java.util.Objects::nonNull)
                .map(item -> item.lineTotal())
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        int totalQty = itemDtos.stream()
                .filter(java.util.Objects::nonNull)
                .mapToInt(item -> item.quantity())
                .sum();
        return new CartResponse(cart.getId(), cart.getUser().getId(), itemDtos, subtotal, totalQty);
    }
}
