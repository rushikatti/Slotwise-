package com.slotwise.controller;

import com.slotwise.config.AuthenticatedUser;
import com.slotwise.dto.ApiResponse;
import com.slotwise.dto.AvailabilityRuleRequest;
import com.slotwise.dto.AvailabilityRuleResponse;
import com.slotwise.dto.SlotResponse;
import com.slotwise.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resources/{resourceId}")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping("/availability-rules")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<ApiResponse<AvailabilityRuleResponse>> addRule(
            @PathVariable Long resourceId,
            @Valid @RequestBody AvailabilityRuleRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        AvailabilityRuleResponse response = availabilityService.addRule(resourceId, user.tenantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/availability-rules")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<ApiResponse<List<AvailabilityRuleResponse>>> listRules(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(
                availabilityService.listRules(resourceId, user.tenantId())));
    }

    @PostMapping("/slots/generate")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> generate(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal AuthenticatedUser user) {
        int created = availabilityService.generateSlots(resourceId, user.tenantId(), from, to);
        return ResponseEntity.ok(ApiResponse.success(Map.of("slotsCreated", created)));
    }

    @GetMapping("/slots")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER','CUSTOMER')")
    public ResponseEntity<ApiResponse<List<SlotResponse>>> listSlots(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(
                availabilityService.listSlots(resourceId, user.tenantId(), date)));
    }
}