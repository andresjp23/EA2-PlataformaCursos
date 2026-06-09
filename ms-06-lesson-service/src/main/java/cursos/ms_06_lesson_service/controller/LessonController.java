package cursos.ms_06_lesson_service.controller;

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

import cursos.ms_06_lesson_service.dto.LessonRequest;
import cursos.ms_06_lesson_service.dto.LessonResponse;
import cursos.ms_06_lesson_service.service.LessonService;
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
@RequestMapping("/lessons")
@RequiredArgsConstructor
@Tag(name = "Lecciones", description = "Gestión de lecciones")
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "Crear lección", description = "Crea una nueva lección en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lección creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la lección a crear", required = true, content = @Content(schema = @Schema(implementation = LessonRequest.class)))
            @Valid @RequestBody LessonRequest request) {
        log.info("POST /lessons -> Creando lección: {}", request.getTitle());
        LessonResponse response = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @Operation(summary = "Obtener lección por ID", description = "Busca y retorna una lección por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lección encontrada"),
        @ApiResponse(responseCode = "404", description = "Lección no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(
            @Parameter(description = "ID de la lección", required = true) @PathVariable Long id) {
        log.info("GET /lessons/{} - Obteniendo lección por ID", id);
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @Operation(summary = "Obtener lecciones por curso", description = "Retorna todas las lecciones asociadas a un curso")
    @ApiResponse(responseCode = "200", description = "Lista de lecciones obtenida exitosamente")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourseId(
            @Parameter(description = "ID del curso", required = true) @PathVariable Long courseId) {
        log.info("GET /lessons/course/{} - Obteniendo lecciones por curso", courseId);
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @Operation(summary = "Obtener todas las lecciones", description = "Retorna una lista de todas las lecciones")
    @ApiResponse(responseCode = "200", description = "Lista de lecciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        log.info("GET /lessons -> Obteniendo todas las lecciones");
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @Operation(summary = "Actualizar lección", description = "Actualiza los datos de una lección existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lección actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonResponse.class))),
        @ApiResponse(responseCode = "404", description = "Lección no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LessonResponse> updateLesson(
            @Parameter(description = "ID de la lección a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos de la lección", required = true, content = @Content(schema = @Schema(implementation = LessonRequest.class)))
            @Valid @RequestBody LessonRequest request) {
        log.info("PUT /lessons/{} - Actualizando lección", id);
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @Operation(summary = "Eliminar lección", description = "Elimina una lección del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Lección eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Lección no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(
            @Parameter(description = "ID de la lección a eliminar", required = true) @PathVariable Long id) {
        log.info("DELETE /lessons/{} - Eliminando lección", id);
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}