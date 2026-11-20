package com.localy.backend.infrastructure.web.controller.bot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.localy.backend.application.chatbot.GeminiService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController  {

    private final GeminiService geminiService;

    @PostMapping
    public Map<String,String> chat(@RequestBody Map<String,String> request) {
        System.out.println("JSON recibido: " + request);
        String mensaje = request.get("message");
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException(
                "El mensaje no puede estar vacío"
            );
        }
        String respuesta = geminiService.generarRespuesta(mensaje);        
        return Map.of("response",respuesta);
    }
}