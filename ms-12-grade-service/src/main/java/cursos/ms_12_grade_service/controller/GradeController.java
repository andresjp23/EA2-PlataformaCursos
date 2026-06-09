package cursos.ms_12_grade_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_12_grade_service.dto.GradeRequest;
import cursos.ms_12_grade_service.dto.GradeResponse;
import cursos.ms_12_grade_service.service.GradeService;
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
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gestión de notas")
public class GradeController {

    private final GradeService gradeService;

    @Operation(summary = "Registrar nota", description = "Registra una nueva nota para un estudiante en una evaluación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Nota registrada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GradeResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<GradeResponse> createGrade(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la nota a registrar", required = true, content = @Content(schema = @Schema(implementation = GradeRequest.class)))
            @Valid @RequestBody GradeRequest request) {
        log.info("POST /grades -> Registrando nota para student: {} evaluation: {}",
                request.getStudentId(), request.getEvaluationId());
        GradeResponse response = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener nota por ID", description = "Busca y retorna una nota por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GradeResponse> getGradeById(
            @Parameter(description = "ID de la nota", required = true) @PathVariable Long id) {
        log.info("GET /grades/{} - Obteniendo nota por ID", id);
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @Operation(summary = "Obtener nota por estudiante y evaluación", description = "Busca la nota específica de un estudiante en una evaluación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/student/{studentId}/evaluation/{evaluationId}")
    public ResponseEntity<GradeResponse> getGradeByStudentAndEvaluation(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Long studentId,
            @Parameter(description = "ID de la evaluación", required = true) @PathVariable Long evaluationId) {
        log.info("GET /grades/student/{}/evaluation/{} - Obteniendo nota especifica",
                studentId, evaluationId);
        return ResponseEntity.ok(gradeService.getGradeByStudentAndEvaluation(studentId, evaluationId));
    }

    @Operation(summary = "Obtener notas por estudiante", description = "Retorna todas las notas de un estudiante")
    @ApiResponse(responseCode = "200", description = "Lista de notas obtenida exitosamente")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeResponse>> getGradesByStudent(
            @Parameter(description = "ID del estudiante", required = true) @PathVariable Long studentId) {
        log.info("GET /grades/student/{} - Obteniendo todas las notas del estudiante", studentId);
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }
}