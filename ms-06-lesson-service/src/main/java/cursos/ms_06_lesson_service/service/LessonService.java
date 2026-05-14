package cursos.ms_06_lesson_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cursos.ms_06_lesson_service.dto.LessonRequest;
import cursos.ms_06_lesson_service.dto.LessonResponse;
import cursos.ms_06_lesson_service.model.entity.Lesson;
import cursos.ms_06_lesson_service.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    // Crear una leccion
    public LessonResponse createLesson(LessonRequest request){
        log.info("Creando leccion con titulo: {}", request.getTitle());

        if (lessonRepository.existsByTitle(request.getTitle())){
            log.warn("Intento de crear leccion con titulo duplicado: {}", request.getTitle());
            throw new RuntimeException("Ya existe una leccion con ese titulo.");
        }

        Lesson lesson = Lesson.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .content(request.getContent())
                .videoUrl(request.getVideoUrl())
                .orderIndex(request.getOrderIndex())
                .durationMinutes(request.getDurationMinutes())
                .isActive(true)
                .build();
        Lesson saved = lessonRepository.save(lesson);
        log.info("Lección creada exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }

    // Obtener leccion por el ID
    public LessonResponse getLessonById(Long id) {
        log.info("Buscando lección con ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Lección no encontrada: {}", id);
                    return new RuntimeException("Lección no encontrada.");
                });
        return toResponse(lesson);
    }

    // Obtener leccion por el ID del curso
    public List<LessonResponse> getLessonsByCourseId(Long courseId) {
        log.info("Obteniendo lecciones del curso: {}", courseId);
        return lessonRepository.findByCourseId(courseId).stream()
                .filter(Lesson::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener todas las lecciones (lista)
    public List<LessonResponse> getAllLessons() {
        log.info("Obteniendo todas las lecciones activas");
        return lessonRepository.findAll().stream()
                .filter(Lesson::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Actualizar una leccion
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        log.info("Actualizando lección con ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Lección no encontrada para actualizar: {}", id);
                    return new RuntimeException("Lección no encontrada.");
                });
        if (!lesson.getTitle().equals(request.getTitle())) {
            if (lessonRepository.existsByTitle(request.getTitle())) {
                log.warn("Intento de actualizar a título duplicado: {}", request.getTitle());
                throw new RuntimeException("Ya existe una lección con ese título.");
            }
        }
        lesson.setCourseId(request.getCourseId());
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setDurationMinutes(request.getDurationMinutes());
        Lesson updated = lessonRepository.save(lesson);
        log.info("Lección actualizada exitosamente con ID: {}", id);
        return toResponse(updated);
    }

    // Eliminar una leccion (soft delete) -> cambia estado de active -> false
    public void deleteLesson(Long id) {
        log.info("Eliminando (soft delete) lección con ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Lección no encontrada para eliminar: {}", id);
                    return new RuntimeException("Lección no encontrada.");
                });
        lesson.setActive(false);
        lessonRepository.save(lesson);
        log.info("Lección eliminada (soft delete) exitosamente con ID: {}", id);
    }

    
    private LessonResponse toResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .courseId(lesson.getCourseId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .orderIndex(lesson.getOrderIndex())
                .durationMinutes(lesson.getDurationMinutes())
                .active(lesson.isActive())
                .build();
    }

}
