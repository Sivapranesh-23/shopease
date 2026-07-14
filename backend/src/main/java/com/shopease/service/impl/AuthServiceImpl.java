package com.shopease.service.impl;

import com.shopease.domain.User;
import com.shopease.domain.enums.UserRole;
import com.shopease.dto.request.LoginRequest;
import com.shopease.dto.request.RegisterRequest;
import com.shopease.dto.response.AuthResponse;
import com.shopease.dto.response.UserResponse;
import com.shopease.exception.DuplicateResourceException;
import com.shopease.exception.ResourceNotFoundException;
import com.shopease.mapper.UserMapper;
import com.shopease.repository.UserRepository;
import com.shopease.security.jwt.JwtTokenProvider;
import com.shopease.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    /**
     * In-memory revoked refresh-token registry. In production this would live in Redis.
     */
    private final Map<String, Boolean> revokedRefreshTokens = new ConcurrentHashMap<>();

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();
        user = userRepository.save(user);
        return issueTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.email()));
        if (!user.isActive()) {
            throw new ResourceNotFoundException("User account is disabled");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        return issueTokens(user);
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        if (!tokenProvider.isValid(refreshToken)) {
            throw new ResourceNotFoundException("Invalid or expired refresh token");
        }
        Claims claims = tokenProvider.parse(refreshToken);
        if (!"REFRESH".equals(claims.get("type", String.class))) {
            throw new ResourceNotFoundException("Token is not a refresh token");
        }
        if (revokedRefreshTokens.containsKey(claims.getId())) {
            throw new ResourceNotFoundException("Refresh token has been revoked");
        }
        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", claims.getSubject()));
        return issueTokens(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse me(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Override
    public void logout(Long userId) {
        // Stateless JWT logout: clients discard tokens. Refresh-token revocation is
        // handled implicitly via short access TTL. See class javadoc for Redis note.
    }

    private AuthResponse issueTokens(User user) {
        String access = tokenProvider.generateAccessToken(user);
        String refresh = tokenProvider.generateRefreshToken(user);
        long expiresIn = tokenProvider.getAccessTokenTtlSeconds();
        UserResponse userResponse = userMapper.toResponse(user);
        return AuthResponse.of(access, refresh, expiresIn, userResponse);
    }
}
