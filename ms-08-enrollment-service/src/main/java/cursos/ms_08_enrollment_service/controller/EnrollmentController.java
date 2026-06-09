package cursos.ms_08_enrollment_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_08_enrollment_service.dto.EnrollmentRequest;
import cursos.ms_08_enrollment_service.dto.EnrollmentResponse;
import cursos.ms_08_enrollment_service.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Tag(name = "Inscripciones", description = "Gestión de inscripciones")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Crear inscripción", description = "Crea una nueva inscripción en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscripción creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la inscripción a crear", required = true, content = @Content(schema = @Schema(implementation = EnrollmentRequest.class)))
            @Valid @RequestBody EnrollmentRequest request) {
        log.info("POST /enrollments -> Creando inscripción para user: {} course: {}",
                request.getUserId(), request.getCourseId());
        EnrollmentResponse response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener inscripción por ID", description = "Busca y retorna una inscripción por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción encontrada"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(
            @Parameter(description = "ID de la inscripción", required = true) @PathVariable Long id) {
        log.info("GET /enrollments/{} - Obteniendo inscripción por ID", id);
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @Operation(summary = "Obtener inscripciones por usuario", description = "Retorna todas las inscripciones de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida exitosamente")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId) {
        log.info("GET /enrollments/user/{} - Obteniendo inscripciones por usuario", userId);
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByUser(userId));
    }

    @Operation(summary = "Obtener inscripciones por curso", description = "Retorna todas las inscripciones asociadas a un curso")
    @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida exitosamente")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourse(
            @Parameter(description = "ID del curso", required = true) @PathVariable Long courseId) {
        log.info("GET /enrollments/course/{} - Obteniendo inscripciones por curso", courseId);
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }

    @Operation(summary = "Obtener todas las inscripciones", description = "Retorna una lista de todas las inscripciones")
    @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        log.info("GET /enrollments -> Obteniendo todas las inscripciones");
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @Operation(summary = "Actualizar estado de inscripción", description = "Actualiza el estado de una inscripción existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<EnrollmentResponse> updateEnrollmentStatus(
            @Parameter(description = "ID de la inscripción", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevo estado (ACTIVE, COMPLETED, CANCELLED)", required = true) @RequestParam String status) {
        log.info("PATCH /enrollments/{}/status - Actualizando estado a: {}", id, status);
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(id, status));
    }

    @Operation(summary = "Cancelar inscripción", description = "Cancela una inscripción del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inscripción cancelada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEnrollment(
            @Parameter(description = "ID de la inscripción a cancelar", required = true) @PathVariable Long id) {
        log.info("DELETE /enrollments/{} - Cancelando inscripción", id);
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}