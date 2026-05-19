package cursos.ms_12_grade_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cursos.ms_12_grade_service.dto.GradeRequest;
import cursos.ms_12_grade_service.dto.GradeResponse;
import cursos.ms_12_grade_service.model.entity.Grade;
import cursos.ms_12_grade_service.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;

    // Crear una nueva nota
    public GradeResponse createGrade(GradeRequest request) {
        log.info("Registrando nota para studentId: {} en evaluationId: {} con score: {}",
                request.getStudentId(), request.getEvaluationId(), request.getScore());

        // Verificar que el estudiante no haya rendido esta evaluacion antes
        if (gradeRepository.existsByStudentIdAndEvaluationIdAndIsActiveTrue(
                request.getStudentId(), request.getEvaluationId())) {
            log.warn("El estudiante {} ya tiene una nota registrada para la evaluacion {}",
                    request.getStudentId(), request.getEvaluationId());
            throw new RuntimeException("El estudiante ya tiene una nota registrada para esta evaluacion.");
        }

        // Crear y guardar la nota
        Grade grade = Grade.builder()
                .studentId(request.getStudentId())
                .evaluationId(request.getEvaluationId())
                .score(request.getScore())
                .takenAt(LocalDateTime.now())
                .isActive(true)
                .build();

        Grade saved = gradeRepository.save(grade);
        log.info("Nota creada exitosamente con ID: {}", saved.getId());

        return toResponse(saved);
    }

    // Obtener nota por ID
    public GradeResponse getGradeById(Long id) {
        log.info("Buscando nota con ID: {}", id);

        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Nota no encontrada: {}", id);
                    return new RuntimeException("Nota no encontrada.");
                });

        return toResponse(grade);
    }

    // Obtener nota de un estudiante en una evaluacion especifica
    public GradeResponse getGradeByStudentAndEvaluation(Long studentId, Long evaluationId) {
        log.info("Buscando nota para studentId: {} y evaluationId: {}", studentId, evaluationId);

        Grade grade = gradeRepository
                .findByStudentIdAndEvaluationIdAndIsActiveTrue(studentId, evaluationId)
                .orElseThrow(() -> {
                    log.warn("No se encontro nota para studentId: {} y evaluationId: {}",
                            studentId, evaluationId);
                    return new RuntimeException("No se encontro la nota del estudiante para esta evaluacion.");
                });

        return toResponse(grade);
    }

    // Listar todas las notas de un estudiante
    public List<GradeResponse> getGradesByStudent(Long studentId) {
        log.info("Obteniendo todas las notas del estudiante: {}", studentId);

        return gradeRepository.findByStudentIdAndIsActiveTrue(studentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Convierte entidad a DTO de respuesta
    private GradeResponse toResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .evaluationId(grade.getEvaluationId())
                .score(grade.getScore())
                .takenAt(grade.getTakenAt())
                .active(grade.isActive())
                .build();
    }
}