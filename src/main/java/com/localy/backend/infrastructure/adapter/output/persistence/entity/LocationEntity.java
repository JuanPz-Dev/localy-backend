package com.localy.backend.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor

@Embeddable
public class LocationEntity {
    @Column(length = 100)
    private String city;
    @Column(length = 250)
    private String meetingAddress;
    private Double latitude;
    private Double longitude;
}