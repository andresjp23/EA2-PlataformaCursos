package cursos.ms_10_evaluation_service.controller;

import java.util.List;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_10_evaluation_service.dto.EvaluationRequest;
import cursos.ms_10_evaluation_service.dto.EvaluationResponse;
import cursos.ms_10_evaluation_service.service.EvaluationService;
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
@RequestMapping("/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @Operation(summary = "Crear evaluación", description = "Crea una nueva evaluación en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Evaluación creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la evaluación a crear", required = true, content = @Content(schema = @Schema(implementation = EvaluationRequest.class)))
            @Valid @RequestBody EvaluationRequest request) {
        log.info("POST /evaluations -> Creando evaluacion: {}", request.getTitle());
        EvaluationResponse response = evaluationService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @Operation(summary = "Obtener evaluación por ID", description = "Busca y retorna una evaluación por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getEvaluationById(
            @Parameter(description = "ID de la evaluación", required = true) @PathVariable Long id) {
        log.info("GET /evaluations/{} - Obteniendo evaluacion por ID", id);
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @Operation(summary = "Obtener evaluaciones por curso", description = "Retorna todas las evaluaciones asociadas a un curso")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByCourseId(
            @Parameter(description = "ID del curso", required = true) @PathVariable Long courseId) {
        log.info("GET /evaluations/course/{} - Obteniendo evaluaciones por curso", courseId);
        return ResponseEntity.ok(evaluationService.getEvaluationsByCourseId(courseId));
    }

    @Operation(summary = "Obtener todas las evaluaciones", description = "Retorna una lista de todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getAllEvaluations() {
        log.info("GET /evaluations -> Obteniendo todas las evaluaciones");
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @Operation(summary = "Actualizar evaluación", description = "Actualiza los datos de una evaluación existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evaluación actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @Parameter(description = "ID de la evaluación a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos de la evaluación", required = true, content = @Content(schema = @Schema(implementation = EvaluationRequest.class)))
            @Valid @RequestBody EvaluationRequest request) {
        log.info("PUT /evaluations/{} - Actualizando evaluacion", id);
        return ResponseEntity.ok(evaluationService.updateEvaluation(id, request));
    }

    @Operation(summary = "Eliminar evaluación", description = "Elimina una evaluación del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Evaluación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(
            @Parameter(description = "ID de la evaluación a eliminar", required = true) @PathVariable Long id) {
        log.info("DELETE /evaluations/{} - Eliminando evaluacion", id);
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }
}