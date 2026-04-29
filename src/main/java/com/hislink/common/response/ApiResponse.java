package com.hislink.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> okMessage(String message) {
        return new ApiResponse<>(true, null, message);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
