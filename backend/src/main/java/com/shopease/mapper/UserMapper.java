package com.shopease.mapper;

import com.shopease.domain.User;
import com.shopease.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "active", source = "active")
    UserResponse toResponse(User user);
}
