package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor

@Embeddable
public class ServiceFeatureEntity {
    @Column(name = "feature_name", nullable = false, length = 100)
    private String name;
    @Column(name = "is_included", nullable = false)
    private boolean included;
}