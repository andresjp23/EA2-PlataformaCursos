package cursos.ms_10_evaluation_service.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import cursos.ms_10_evaluation_service.client.CourseClient;
import cursos.ms_10_evaluation_service.client.UserClient;
import cursos.ms_10_evaluation_service.dto.EvaluationRequest;
import cursos.ms_10_evaluation_service.dto.EvaluationResponse;
import cursos.ms_10_evaluation_service.model.entity.Evaluation;
import cursos.ms_10_evaluation_service.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserClient userClient;
    private final CourseClient courseClient;

    public EvaluationResponse createEvaluation(EvaluationRequest request) {
        log.info("Creando evaluacion con titulo: {} para curso: {}", request.getTitle(), request.getCourseId());

        if (evaluationRepository.existsByTitle(request.getTitle())) {
            log.warn("Ya existe una evaluacion con el titulo: {}", request.getTitle());
            throw new RuntimeException("Ya existe una evaluacion con ese titulo.");
        }

        try {
            courseClient.getCourseById(request.getCourseId());
            log.info("Curso {} validado correctamente", request.getCourseId());
        } catch (Exception e) {
            log.error("Error al validar curso {}: {}", request.getCourseId(), e.getMessage());
            throw new RuntimeException("No se encontro el curso especificado.");
        }

        int maxScore = request.getMaxScore() != null ? request.getMaxScore() : 100;
        int passingScore = request.getPassingScore() != null ? request.getPassingScore() : 70;

        Evaluation evaluation = Evaluation.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .maxScore(maxScore)
                .passingScore(passingScore)
                .status("PUBLISHED")
                .isActive(true)
                .build();

        Evaluation saved = evaluationRepository.save(evaluation);
        log.info("Evaluacion creada exitosamente con ID: {}", saved.getId());

        return toResponse(saved);
    }

    public EvaluationResponse getEvaluationById(Long id) {
        log.info("Buscando evaluacion con ID: {}", id);

        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluacion no encontrada: {}", id);
                    return new RuntimeException("Evaluacion no encontrada.");
                });

        return toResponse(evaluation);
    }

    public List<EvaluationResponse> getEvaluationsByCourseId(Long courseId) {
        log.info("Obteniendo evaluaciones del curso: {}", courseId);

        return evaluationRepository.findByCourseId(courseId).stream()
                .filter(Evaluation::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EvaluationResponse> getAllEvaluations() {
        log.info("Obteniendo todas las evaluaciones activas");

        return evaluationRepository.findAll().stream()
                .filter(Evaluation::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EvaluationResponse updateEvaluation(Long id, EvaluationRequest request) {
        log.info("Actualizando evaluacion con ID: {}", id);

        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluacion no encontrada para actualizar: {}", id);
                    return new RuntimeException("Evaluacion no encontrada.");
                });

        evaluation.setCourseId(request.getCourseId());
        evaluation.setTitle(request.getTitle());
        evaluation.setDescription(request.getDescription());
        if (request.getMaxScore() != null) evaluation.setMaxScore(request.getMaxScore());
        if (request.getPassingScore() != null) evaluation.setPassingScore(request.getPassingScore());

        Evaluation updated = evaluationRepository.save(evaluation);
        log.info("Evaluacion actualizada exitosamente con ID: {}", id);

        return toResponse(updated);
    }

    public void deleteEvaluation(Long id) {
        log.info("Eliminando (soft delete) evaluacion con ID: {}", id);

        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Evaluacion no encontrada para eliminar: {}", id);
                    return new RuntimeException("Evaluacion no encontrada.");
                });

        evaluation.setActive(false);
        evaluationRepository.save(evaluation);
        log.info("Evaluacion eliminada (soft delete) exitosamente con ID: {}", id);
    }

    private EvaluationResponse toResponse(Evaluation evaluation) {
        return EvaluationResponse.builder()
                .id(evaluation.getId())
                .courseId(evaluation.getCourseId())
                .title(evaluation.getTitle())
                .description(evaluation.getDescription())
                .maxScore(evaluation.getMaxScore())
                .passingScore(evaluation.getPassingScore())
                .status(evaluation.getStatus())
                .active(evaluation.isActive())
                .build();
    }
}