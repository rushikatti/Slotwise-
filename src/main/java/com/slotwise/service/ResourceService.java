package com.slotwise.service;

import com.slotwise.dto.PagedResponse;
import com.slotwise.dto.ResourceRequest;
import com.slotwise.dto.ResourceResponse;
import com.slotwise.entity.Resource;
import com.slotwise.entity.Tenant;
import com.slotwise.exception.ResourceNotFoundException;
import com.slotwise.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceResponse create(Long tenantId, ResourceRequest request) {
        // a Tenant holding only the id is enough to set the tenant_id foreign key
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);

        Resource resource = new Resource();
        resource.setName(request.name());
        resource.setType(request.type());
        resource.setTenant(tenant);

        return ResourceResponse.from(resourceRepository.save(resource));
    }

    @Transactional(readOnly = true)
    public ResourceResponse getForTenant(Long id, Long tenantId) {
        // finds the row only if it belongs to this tenant, so other tenants' rows are invisible
        return resourceRepository.findByIdAndTenantId(id, tenantId)
                .map(ResourceResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    @Transactional(readOnly = true)
    public PagedResponse<ResourceResponse> list(Long tenantId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);   // between 1 and 100
        var pageable = PageRequest.of(safePage, safeSize, Sort.by("id"));
        return PagedResponse.from(
                resourceRepository.findByTenantId(tenantId, pageable).map(ResourceResponse::from));
    }
}