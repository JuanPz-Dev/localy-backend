package com.localy.backend.infrastructure.web.controller.modelo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.service.DemandPredictionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class DemandPredictionController {
    
    private final DemandPredictionService demandPredictionService;

    @GetMapping("/demand/{tourId}")
    public ResponseEntity<?> predecirDemanda(@PathVariable Long tourId) {
        return ResponseEntity.ok(demandPredictionService.predecir(tourId)
        );
    }

    @GetMapping("/demand/batch")
    public ResponseEntity<?> predecirTodas() {
        return ResponseEntity.ok(demandPredictionService.predecirTodas());
    }
    
}
