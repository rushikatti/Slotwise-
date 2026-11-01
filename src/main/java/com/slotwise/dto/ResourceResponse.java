package com.slotwise.dto;

import com.slotwise.entity.Resource;

public record ResourceResponse(Long id, String name, String type) {
    public static ResourceResponse from(Resource r) {
        return new ResourceResponse(r.getId(), r.getName(), r.getType());
    }
}