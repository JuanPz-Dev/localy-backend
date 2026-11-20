package com.localy.backend.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import com.localy.backend.application.port.output.ReservationRepositoryPort;
import com.localy.backend.application.port.output.TourServiceRepositoryPort;
import com.localy.backend.domain.enums.ReservationStatus;
import com.localy.backend.domain.model.Reservation;
import com.localy.backend.domain.model.TourSchedule;
import com.localy.backend.domain.model.TourService;
import com.localy.backend.infrastructure.adapter.output.prediction.FastApiPredictionClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DemandPredictionService {

    private final ReservationRepositoryPort reservationRepository;
    private final TourServiceRepositoryPort tourServiceRepository;
    private final FastApiPredictionClient predictionClient;

    // Resultado de cada experiencia en la consulta masiva
    public record PrediccionLote(
        Long tourId,
        JsonNode prediccion
    ) {}

    // Predicción individual: conservamos el endpoint existente
    @Transactional(readOnly = true)
    public Object predecir(Long tourId) {

        TourService tour = tourServiceRepository.findById(tourId)
            .orElseThrow(() ->
                new IllegalArgumentException("Experiencia no encontrada"));

        List<Reservation> reservas =
            reservationRepository.findByTourServiceId(tourId);

        var request = construirSolicitud(tour, reservas, LocalDate.now());

        return predictionClient.predecir(request);
    }

    // Nueva predicción masiva para Analítica
    @Transactional(readOnly = true)
public List<PrediccionLote> predecirTodas() {

    // 1. Obtener todas las experiencias
    List<TourService> tours =
        tourServiceRepository.findAllForPrediction();

    if (tours.isEmpty()) {
        return List.of();
    }

    // 2. Obtener los identificadores
    List<Long> tourIds = tours.stream()
        .map(TourService::getId)
        .toList();

    // 3. Obtener todas las reservas
    List<Reservation> reservas =
        reservationRepository.findByTourServiceIds(tourIds);

    // 4. Agrupar las reservas por experiencia
    Map<Long, List<Reservation>> reservasPorTour =
        reservas.stream().collect(
            Collectors.groupingBy(
                reserva -> reserva.getTourService().getId()
            )
        );

    // 5. Construir todas las solicitudes para FastAPI
    LocalDate hoy = LocalDate.now();

    List<FastApiPredictionClient.ExperienciaRequest> solicitudes =
        new ArrayList<>();

    for (TourService tour : tours) {

        List<Reservation> reservasTour =
            reservasPorTour.getOrDefault(
                tour.getId(),
                List.of()
            );

        var request = construirSolicitud(
            tour,
            reservasTour,
            hoy
        );

        solicitudes.add(request);
    }

    // 6. Enviar todas las experiencias en una sola petición
    JsonNode resultados =
        predictionClient.predecirLote(solicitudes);

    if (resultados == null || !resultados.isArray()) {
        throw new IllegalStateException(
            "FastAPI no devolvió una lista de predicciones."
        );
    }

    if (resultados.size() != tours.size()) {
        throw new IllegalStateException(
            "La cantidad de predicciones recibidas no coincide "
            + "con la cantidad de experiencias."
        );
    }

    // 7. Asociar cada resultado con su experiencia
    List<PrediccionLote> respuesta = new ArrayList<>();

    for (int i = 0; i < tours.size(); i++) {

        respuesta.add(
            new PrediccionLote(
                tours.get(i).getId(),
                resultados.get(i)
            )
        );
    }
    return respuesta;
}
    // Construye las variables que recibe el modelo
    private FastApiPredictionClient.ExperienciaRequest construirSolicitud(
            TourService tour,
            List<Reservation> reservas,
            LocalDate hoy) {

        LocalDate inicioSemanaActual =
            hoy.with(DayOfWeek.MONDAY);

        LocalDate inicioSemanaAnterior =
            inicioSemanaActual.minusWeeks(1);

        LocalDate inicioHaceDosSemanas =
            inicioSemanaActual.minusWeeks(2);

        LocalDate inicioHaceTresSemanas =
            inicioSemanaActual.minusWeeks(3);

        int confirmadas = 0;
        int pendientes = 0;
        int canceladas = 0;
        int confirmadasSemanaAnterior = 0;
        int confirmadasHaceDosSemanas = 0;
        int canceladasSemanaAnterior = 0;

        int[] confirmadasPorSemana = new int[3];

        for (Reservation reserva : reservas) {

            LocalDate fecha = reserva.getReservationDate();

            if (fecha == null) {
                continue;
            }

            ReservationStatus estado =
                reserva.getReservationStatus();

            boolean confirmada =
                estado == ReservationStatus.PAID
                || estado == ReservationStatus.COMPLETED;

            if (confirmada) {
                confirmadas++;
            } else if (estado == ReservationStatus.PENDING) {
                pendientes++;
            } else if (estado == ReservationStatus.CANCELLED) {
                canceladas++;
            }

            if (confirmada
                    && !fecha.isBefore(inicioSemanaAnterior)
                    && fecha.isBefore(inicioSemanaActual)) {
                confirmadasSemanaAnterior++;
            }

            if (confirmada
                    && !fecha.isBefore(inicioHaceDosSemanas)
                    && fecha.isBefore(inicioSemanaAnterior)) {
                confirmadasHaceDosSemanas++;
            }

            if (estado == ReservationStatus.CANCELLED
                    && !fecha.isBefore(inicioSemanaAnterior)
                    && fecha.isBefore(inicioSemanaActual)) {
                canceladasSemanaAnterior++;
            }

            if (confirmada
                    && !fecha.isBefore(inicioHaceTresSemanas)
                    && fecha.isBefore(inicioSemanaActual)) {

                int indice = (int) ChronoUnit.WEEKS.between(
                    inicioHaceTresSemanas,
                    fecha
                );

                if (indice >= 0 && indice < 3) {
                    confirmadasPorSemana[indice]++;
                }
            }
        }

        double promedioTresSemanas =
            (confirmadasPorSemana[0]
            + confirmadasPorSemana[1]
            + confirmadasPorSemana[2]) / 3.0;

        List<TourSchedule> horarios =
            tour.getSchedules() == null
                ? List.of()
                : tour.getSchedules();

        int cantidadHorarios = horarios.size();

        long diasDisponibles = horarios.stream()
            .map(TourSchedule::getDay)
            .filter(dia -> dia != null)
            .distinct()
            .count();

        double horaPromedio = horarios.stream()
            .map(TourSchedule::getStartTime)
            .filter(hora -> hora != null)
            .mapToInt(LocalTime::getHour)
            .average()
            .orElse(0.0);

        return new FastApiPredictionClient.ExperienciaRequest(
            tour.getCategory().name(),
            tour.getPrice().doubleValue(),
            tour.getDurationMinutes(),
            tour.getMinParticipants(),
            tour.getMaxParticipants(),
            cantidadHorarios,
            (int) diasDisponibles,
            horaPromedio,
            confirmadas,
            pendientes,
            canceladas,
            confirmadasSemanaAnterior,
            confirmadasHaceDosSemanas,
            promedioTresSemanas,
            canceladasSemanaAnterior
        );
    }
}
