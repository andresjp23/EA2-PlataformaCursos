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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping
    public ResponseEntity<EvaluationResponse> createEvaluation(@Valid @RequestBody EvaluationRequest request) {
        log.info("POST /evaluations -> Creando evaluacion: {}", request.getTitle());
        EvaluationResponse response = evaluationService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getEvaluationById(@PathVariable Long id) {
        log.info("GET /evaluations/{} - Obteniendo evaluacion por ID", id);
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EvaluationResponse>> getEvaluationsByCourseId(@PathVariable Long courseId) {
        log.info("GET /evaluations/course/{} - Obteniendo evaluaciones por curso", courseId);
        return ResponseEntity.ok(evaluationService.getEvaluationsByCourseId(courseId));
    }

    @GetMapping
    public ResponseEntity<List<EvaluationResponse>> getAllEvaluations() {
        log.info("GET /evaluations -> Obteniendo todas las evaluaciones");
        return ResponseEntity.ok(evaluationService.getAllEvaluations());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationRequest request) {
        log.info("PUT /evaluations/{} - Actualizando evaluacion", id);
        return ResponseEntity.ok(evaluationService.updateEvaluation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        log.info("DELETE /evaluations/{} - Eliminando evaluacion", id);
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }
}