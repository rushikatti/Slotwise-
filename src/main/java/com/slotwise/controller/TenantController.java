package com.slotwise.controller;

import com.slotwise.dto.ApiResponse;
import com.slotwise.dto.CreateTenantRequest;
import com.slotwise.dto.TenantResponse;
import com.slotwise.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;




@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantController {
 
    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

  
@GetMapping("/{id}")

@PreAuthorize("hasRole('SUPER_ADMIN') or #id == principal.tenantId()")
public ResponseEntity<?> getTenant(@PathVariable Long id) {
    TenantResponse response = tenantService.getTenantById(id);
    return ResponseEntity.ok(ApiResponse.success(response));
}

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> listTenants() {
        List<TenantResponse> responses = tenantService.getAllTenants();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}