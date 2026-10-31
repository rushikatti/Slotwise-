package com.slotwise.dto;

import com.slotwise.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private Long tenantId;
}