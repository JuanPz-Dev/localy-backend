package com.localy.backend.domain.model;

import java.util.List;

import com.localy.backend.domain.enums.GuideAvailability;
import com.localy.backend.domain.enums.GuideStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

@Builder(toBuilder = true)
public class GuideProfile{
    private Long id;
    private User user;
    private String phone;
    private String address;
    private String license;
    private String description;
    private String cv;
    private Double averageRating;
    private Integer reviewCount;
    private List<String> languages;
    private GuideAvailability guideAvailability;
    private GuideStatus guideStatus;
}