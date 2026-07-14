package com.shopease.service;

import com.shopease.domain.enums.UserRole;
import com.shopease.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    Page<UserResponse> listAll(Pageable pageable);
    
    UserResponse getById(Long id);
    
    UserResponse updateStatus(Long id, boolean active);
    
    UserResponse updateRole(Long id, UserRole role);
    
    void delete(Long id);
}
