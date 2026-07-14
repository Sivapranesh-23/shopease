package com.shopease.dto.response;

/**
 * Generic envelope for single-resource responses carrying a message.
 *
 * @param success whether the operation succeeded
 * @param message human-readable message
 * @param data    optional payload
 */
public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> message(String message) {
        return new ApiResponse<>(true, message, null);
    }
}
