package com.shopease.mapper;

import com.shopease.domain.Order;
import com.shopease.domain.OrderItem;
import com.shopease.dto.response.OrderItemResponse;
import com.shopease.dto.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", imports = BigDecimal.class)
public interface OrderMapper {

    @Mapping(target = "lineTotal", expression = "java(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))")
    OrderItemResponse toItemResponse(OrderItem item);

    List<OrderItemResponse> toItemResponseList(List<OrderItem> items);

    @Mapping(target = "items", source = "items")
    OrderResponse toResponse(Order order);
}
