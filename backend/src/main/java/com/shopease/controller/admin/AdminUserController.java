package com.shopease.controller.admin;

import com.shopease.dto.response.ApiResponse;
import com.shopease.dto.response.UserResponse;
import com.shopease.mapper.UserMapper;
import com.shopease.repository.UserRepository;
import com.shopease.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Admin — Users")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Operation(summary = "List all users")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> users = userRepository.findAll(PageRequest.of(page, size))
                .map(userMapper::toResponse);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> get(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(u))))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Activate or suspend a user")
    @PatchMapping("/{id}/active")
    public ResponseEntity<ApiResponse<UserResponse>> setActive(
            @PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setActive(Boolean.TRUE.equals(body.get("active")));
        return ResponseEntity.ok(ApiResponse.success("User status updated",
                userMapper.toResponse(userRepository.save(user))));
    }
}
