package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Embeddable
public class LocationEntity {
    private String city;
    private String meetingAddress;
    private Double latitude;
    private Double longitude;
}