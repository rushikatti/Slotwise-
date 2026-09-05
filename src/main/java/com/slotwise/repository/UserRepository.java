package com.slotwise.repository;

import com.slotwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByTenantId(Long tenantId);
    Optional<User> findByEmail(String email);
}