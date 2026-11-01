package com.slotwise.controller;

import com.slotwise.config.AuthenticatedUser;
import com.slotwise.dto.ApiResponse;
import com.slotwise.dto.PagedResponse;
import com.slotwise.dto.ResourceRequest;
import com.slotwise.dto.ResourceResponse;
import com.slotwise.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<ApiResponse<ResourceResponse>> create(
            @Valid @RequestBody ResourceRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        ResourceResponse response = resourceService.create(user.tenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER','CUSTOMER')")
    public ResponseEntity<ApiResponse<ResourceResponse>> get(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.getForTenant(id, user.tenantId())));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER','CUSTOMER')")
    public ResponseEntity<ApiResponse<PagedResponse<ResourceResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(resourceService.list(user.tenantId(), page, size)));
    }
}