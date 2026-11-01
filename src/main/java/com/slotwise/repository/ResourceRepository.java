package com.slotwise.repository;

import com.slotwise.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByTenantId(Long tenantId);

    // paged list, limited to one tenant
    Page<Resource> findByTenantId(Long tenantId, Pageable pageable);

    // find one, but only if it belongs to this tenant (otherwise empty -> 404)
    Optional<Resource> findByIdAndTenantId(Long id, Long tenantId);
}