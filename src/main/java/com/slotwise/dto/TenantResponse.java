package com.slotwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class TenantResponse {
    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}