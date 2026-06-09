package cursos.ms_09_progress_service.controller;

import java.util.List;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_09_progress_service.dto.ProgressRequest;
import cursos.ms_09_progress_service.dto.ProgressResponse;
import cursos.ms_09_progress_service.service.ProgressService;
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
@RequestMapping("/progress")
@RequiredArgsConstructor
@Tag(name = "Progreso", description = "Gestión de progreso de usuarios")
public class ProgressController {

    private final ProgressService progressService;

    @Operation(summary = "Crear progreso", description = "Crea un nuevo registro de progreso para un usuario en un curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Progreso creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgressResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ProgressResponse> createProgress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del progreso a crear", required = true, content = @Content(schema = @Schema(implementation = ProgressRequest.class)))
            @Valid @RequestBody ProgressRequest request) {
        log.info("POST /progress -> Creando progreso para usuario {} en curso {}",
                request.getUserId(), request.getCourseId());
        ProgressResponse response = progressService.createProgress(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @Operation(summary = "Obtener progreso por ID", description = "Busca y retorna un progreso por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progreso encontrado"),
        @ApiResponse(responseCode = "404", description = "Progreso no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProgressResponse> getProgressById(
            @Parameter(description = "ID del progreso", required = true) @PathVariable Long id) {
        log.info("GET /progress/{} - Obteniendo progreso por ID", id);
        return ResponseEntity.ok(progressService.getProgressById(id));
    }

    @Operation(summary = "Obtener progreso por usuario y curso", description = "Busca el progreso de un usuario en un curso específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progreso encontrado"),
        @ApiResponse(responseCode = "404", description = "Progreso no encontrado")
    })
    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<ProgressResponse> getProgressByUserAndCourse(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId,
            @Parameter(description = "ID del curso", required = true) @PathVariable Long courseId) {
        log.info("GET /progress/user/{}/course/{} - Obteniendo progreso", userId, courseId);
        return ResponseEntity.ok(progressService.getProgressByUserAndCourse(userId, courseId));
    }

    @Operation(summary = "Obtener progresos por usuario", description = "Retorna todos los progresos de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de progresos obtenida exitosamente")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProgressResponse>> getProgressByUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId) {
        log.info("GET /progress/user/{} - Obteniendo todos los progresos del usuario", userId);
        return ResponseEntity.ok(progressService.getProgressByUser(userId));
    }

    @Operation(summary = "Marcar lección como completada", description = "Marca una lección específica como completada dentro del progreso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lección marcada como completada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgressResponse.class))),
        @ApiResponse(responseCode = "404", description = "Progreso no encontrado")
    })
    @PatchMapping("/{progressId}/complete")
    public ResponseEntity<ProgressResponse> completeLesson(
            @Parameter(description = "ID del progreso", required = true) @PathVariable Long progressId,
            @Parameter(description = "ID de la lección completada", required = true) @RequestParam Long lessonId) {
        log.info("PATCH /progress/{}/complete?lessonId={} - Marcando leccion como completada",
                progressId, lessonId);
        return ResponseEntity.ok(progressService.completeLesson(progressId, lessonId));
    }

    @Operation(summary = "Actualizar estado del progreso", description = "Actualiza el estado de un progreso existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgressResponse.class))),
        @ApiResponse(responseCode = "404", description = "Progreso no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProgressResponse> updateProgressStatus(
            @Parameter(description = "ID del progreso", required = true) @PathVariable Long id,
            @Parameter(description = "Nuevo estado del progreso", required = true) @RequestParam String status) {
        log.info("PATCH /progress/{}/status?status={} - Actualizando estado", id, status);
        return ResponseEntity.ok(progressService.updateProgressStatus(id, status));
    }

    @Operation(summary = "Eliminar progreso", description = "Elimina un registro de progreso del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Progreso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Progreso no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(
            @Parameter(description = "ID del progreso a eliminar", required = true) @PathVariable Long id) {
        log.info("DELETE /progress/{} - Eliminando progreso", id);
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}