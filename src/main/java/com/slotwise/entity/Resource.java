package com.slotwise.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resources")
@Data
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g. "Dr. Sharma", "Meeting Room A"

    private String type; // e.g. "DOCTOR", "ROOM", "DESK" — simple string for now

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}