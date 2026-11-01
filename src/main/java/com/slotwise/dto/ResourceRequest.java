package com.slotwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResourceRequest(
        @NotBlank(message = "Resource name is required") @Size(max = 255) String name,
        @NotBlank(message = "Resource type is required") @Size(max = 50) String type) {}