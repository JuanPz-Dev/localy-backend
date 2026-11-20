package com.localy.backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Location {
    private String city;
    private String meetingAddress;
    private Double latitude;
    private Double longitude;
}