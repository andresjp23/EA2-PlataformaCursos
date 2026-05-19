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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // POST /grades - Registrar una nota
    @PostMapping
    public ResponseEntity<GradeResponse> createGrade(@Valid @RequestBody GradeRequest request) {
        log.info("POST /grades -> Registrando nota para student: {} evaluation: {}",
                request.getStudentId(), request.getEvaluationId());
        GradeResponse response = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /grades/{id} - Obtener nota por ID
    @GetMapping("/{id}")
    public ResponseEntity<GradeResponse> getGradeById(@PathVariable Long id) {
        log.info("GET /grades/{} - Obteniendo nota por ID", id);
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    // GET /grades/student/{studentId}/evaluation/{evaluationId} - Obtener nota especifica
    @GetMapping("/student/{studentId}/evaluation/{evaluationId}")
    public ResponseEntity<GradeResponse> getGradeByStudentAndEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long evaluationId) {
        log.info("GET /grades/student/{}/evaluation/{} - Obteniendo nota especifica",
                studentId, evaluationId);
        return ResponseEntity.ok(gradeService.getGradeByStudentAndEvaluation(studentId, evaluationId));
    }

    // GET /grades/student/{studentId} - Listar todas las notas de un estudiante
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeResponse>> getGradesByStudent(@PathVariable Long studentId) {
        log.info("GET /grades/student/{} - Obteniendo todas las notas del estudiante", studentId);
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }
}