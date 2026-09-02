package com.slotwise.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {
    
    private final boolean success;
    private final T data;
    private final String error;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, String error){
        this.success = success;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(true, data,null);
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(false, null, message);
    }
}
