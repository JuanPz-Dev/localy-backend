package com.localy.backend.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor

public class Review {
    private Long id;
    private User user;
    private TourService tourService;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}