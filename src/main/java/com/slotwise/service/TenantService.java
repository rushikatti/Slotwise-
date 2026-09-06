package com.slotwise.service;

import com.slotwise.dto.CreateTenantRequest;
import com.slotwise.dto.TenantResponse;
import com.slotwise.entity.Tenant;
import com.slotwise.exception.DuplicateResourceException;
import com.slotwise.exception.ResourceNotFoundException;
import com.slotwise.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;




@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantResponse createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Tenant slug already in use");
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setSlug(request.getSlug());

        Tenant saved = tenantRepository.save(tenant);
        return toResponse(saved);
    }

    public TenantResponse getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        return toResponse(tenant);
    }

    public List<TenantResponse> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getName(),
                tenant.getSlug(),
                tenant.getCreatedAt()
        );
    }
}