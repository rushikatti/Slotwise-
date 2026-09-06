package com.slotwise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTenantRequest {

    @NotBlank(message = "Tenant name is required")
    private String name;

    @NotBlank(message = "Tenant slug is required")
    private String slug; // e.g. "acme-gym" - used in URLs later, must be unique
}