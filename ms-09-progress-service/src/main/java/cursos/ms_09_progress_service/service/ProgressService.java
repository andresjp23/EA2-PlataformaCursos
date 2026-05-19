package cursos.ms_09_progress_service.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import cursos.ms_09_progress_service.client.CourseClient;
import cursos.ms_09_progress_service.client.LessonClient;
import cursos.ms_09_progress_service.dto.ProgressRequest;
import cursos.ms_09_progress_service.dto.ProgressResponse;
import cursos.ms_09_progress_service.model.entity.Progress;
import cursos.ms_09_progress_service.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final LessonClient lessonClient;
    private final CourseClient courseClient;

    public ProgressResponse createProgress(ProgressRequest request) {
        log.info("Creando progreso para userId: {} en courseId: {}", request.getUserId(), request.getCourseId());

        try {
            courseClient.getCourseById(request.getCourseId());
            log.info("Curso {} validado correctamente", request.getCourseId());
        } catch (Exception e) {
            log.error("Error al validar curso {}: {}", request.getCourseId(), e.getMessage());
            throw new RuntimeException("No se encontro el curso especificado.");
        }

        if (progressRepository.existsByUserIdAndCourseIdAndIsActiveTrue(request.getUserId(), request.getCourseId())) {
            log.warn("El usuario {} ya tiene progreso en el curso {}", request.getUserId(), request.getCourseId());
            throw new RuntimeException("El usuario ya tiene progreso en este curso.");
        }

        // Obtener cantidad de lecciones del curso via Feign
        int totalLessons = 0;
        try {
            Object lessonsObj = lessonClient.getLessonsByCourseId(request.getCourseId());
            if (lessonsObj instanceof List) {
                totalLessons = ((List<?>) lessonsObj).size();
                log.info("El curso {} tiene {} lecciones", request.getCourseId(), totalLessons);
            }
        } catch (Exception e) {
            log.error("Error al obtener lecciones del curso {}: {}", request.getCourseId(), e.getMessage());
        }

        Progress progress = Progress.builder()
                .userId(request.getUserId())
                .courseId(request.getCourseId())
                .currentLessonId(request.getCurrentLessonId())
                .completedLessons(0)
                .totalLessons(totalLessons)
                .progressPercentage(0)
                .status("ACTIVE")
                .isActive(true)
                .build();

        Progress saved = progressRepository.save(progress);
        log.info("Progreso creado exitosamente con ID: {}", saved.getId());

        return toResponse(saved);
    }

    public ProgressResponse getProgressById(Long id) {
        log.info("Buscando progreso con ID: {}", id);

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Progreso no encontrado: {}", id);
                    return new RuntimeException("Progreso no encontrado.");
                });

        return toResponse(progress);
    }

    public ProgressResponse getProgressByUserAndCourse(Long userId, Long courseId) {
        log.info("Buscando progreso del usuario {} en curso {}", userId, courseId);

        Progress progress = progressRepository.findByUserIdAndCourseIdAndIsActiveTrue(userId, courseId)
                .orElseThrow(() -> {
                    log.warn("No se encontro progreso para usuario {} en curso {}", userId, courseId);
                    return new RuntimeException("Progreso no encontrado.");
                });

        return toResponse(progress);
    }

    public List<ProgressResponse> getProgressByUser(Long userId) {
        log.info("Obteniendo progresos del usuario: {}", userId);

        return progressRepository.findByUserId(userId).stream()
                .filter(Progress::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProgressResponse completeLesson(Long progressId, Long lessonId) {
        log.info("Marcando leccion {} como completada en progreso {}", lessonId, progressId);

        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> {
                    log.warn("Progreso no encontrado: {}", progressId);
                    return new RuntimeException("Progreso no encontrado.");
                });

        progress.setCurrentLessonId(lessonId);
        progress.setCompletedLessons(progress.getCompletedLessons() + 1);

        int total = progress.getTotalLessons();
        int completadas = progress.getCompletedLessons();
        int porcentaje = (total > 0) ? (completadas * 100 / total) : 0;
        progress.setProgressPercentage(porcentaje);

        if (completadas >= total && total > 0) {
            progress.setStatus("COMPLETED");
            log.info("Curso {} completado por usuario {}", progress.getCourseId(), progress.getUserId());
        }

        Progress updated = progressRepository.save(progress);
        log.info("Progreso actualizado. Porcentaje: {}%", porcentaje);

        return toResponse(updated);
    }

    public ProgressResponse updateProgressStatus(Long id, String status) {
        log.info("Actualizando estado de progreso {} a: {}", id, status);

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Progreso no encontrado para actualizar: {}", id);
                    return new RuntimeException("Progreso no encontrado.");
                });

        progress.setStatus(status);
        Progress updated = progressRepository.save(progress);
        log.info("Estado de progreso actualizado a: {}", status);

        return toResponse(updated);
    }

    public void deleteProgress(Long id) {
        log.info("Eliminando (soft delete) progreso con ID: {}", id);

        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Progreso no encontrado para eliminar: {}", id);
                    return new RuntimeException("Progreso no encontrado.");
                });

        progress.setActive(false);
        progressRepository.save(progress);
        log.info("Progreso eliminado (soft delete) exitosamente con ID: {}", id);
    }

    private ProgressResponse toResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .userId(progress.getUserId())
                .courseId(progress.getCourseId())
                .currentLessonId(progress.getCurrentLessonId())
                .completedLessons(progress.getCompletedLessons())
                .totalLessons(progress.getTotalLessons())
                .progressPercentage(progress.getProgressPercentage())
                .status(progress.getStatus())
                .active(progress.isActive())
                .build();
    }
}