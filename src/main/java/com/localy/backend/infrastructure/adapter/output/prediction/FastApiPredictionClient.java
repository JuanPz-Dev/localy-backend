package com.localy.backend.infrastructure.adapter.output.prediction;

import tools.jackson.databind.JsonNode;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FastApiPredictionClient {

    private final RestClient restClient;

    public FastApiPredictionClient(@Value("${localy.ml.base-url:http://localhost:8000}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public JsonNode predecir(ExperienciaRequest request) {
        return restClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(JsonNode.class);
    }
    public JsonNode predecirLote(List<ExperienciaRequest> requests) {
        return restClient.post()
        .uri("/predict/batch")
        .contentType(MediaType.APPLICATION_JSON)
        .body(requests)
        .retrieve()
        .body(JsonNode.class);
        }


    public record ExperienciaRequest(
            String categoria,
            double precio,
            int duracion_minutos,
            int participantes_minimos,
            int participantes_maximos,
            int cantidad_horarios,
            int dias_disponibles,
            double hora_promedio,
            int reservas_confirmadas,
            int reservas_pendientes,
            int reservas_canceladas,
            int confirmadas_semana_anterior,
            int confirmadas_hace_dos_semanas,
            double promedio_confirmadas_ultimas_3_semanas,
            int canceladas_semana_anterior
    ) {}
}