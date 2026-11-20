package com.localy.backend.application.chatbot;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;

@Service
public class GeminiService {
    
    private RestClient restClient;
    private final String apikey;
    private final String apiUrl;

    public GeminiService(RestClient restClient, @Value("${GEMINI_API_KEY}") String apikey, 
                         @Value("${gemini.api.url}")String apiUrl) {
        this.restClient = restClient;
        this.apikey = apikey;
        this.apiUrl = apiUrl;
    }

    public String generarRespuesta(String mensaje) {

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(
                        Map.of("text",
                            """
                            Eres el asistente virtual de Localy,
                            una plataforma turística de Cartagena de Indias
                            que conecta viajeros con guías independientes.

                            Responde en español, de forma amable,
                            clara y breve.
                            Ayuda con preguntas sobre turismo,
                            experiencias y recomendaciones en Cartagena.

                            No inventes precios, disponibilidad ni
                            información específica de tours de Localy.
                            Si no conoces un dato, indícalo claramente.

                            Mensaje del usuario:
                            """ + mensaje
                        )
                    )
                )
            )
        );

        JsonNode response = restClient.post()
            .uri(apiUrl)
            .header("x-goog-api-key", apikey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException(
                "Gemini no devolvió una respuesta."
            );
        }
        JsonNode textNode = response
            .path("candidates")
            .path(0)
            .path("content")
            .path("parts")
            .path(0)
            .path("text");

        if (textNode.isMissingNode() || textNode.isNull()) {
            throw new IllegalStateException(
                "Gemini no generó una respuesta de texto."
            );
        }
        return textNode.asString();
    }
}