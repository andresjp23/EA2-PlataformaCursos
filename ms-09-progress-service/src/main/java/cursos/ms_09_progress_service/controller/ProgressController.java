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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @PostMapping
    public ResponseEntity<ProgressResponse> createProgress(@Valid @RequestBody ProgressRequest request) {
        log.info("POST /progress -> Creando progreso para usuario {} en curso {}",
                request.getUserId(), request.getCourseId());
        ProgressResponse response = progressService.createProgress(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressResponse> getProgressById(@PathVariable Long id) {
        log.info("GET /progress/{} - Obteniendo progreso por ID", id);
        return ResponseEntity.ok(progressService.getProgressById(id));
    }

    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<ProgressResponse> getProgressByUserAndCourse(
            @PathVariable Long userId,
            @PathVariable Long courseId) {
        log.info("GET /progress/user/{}/course/{} - Obteniendo progreso", userId, courseId);
        return ResponseEntity.ok(progressService.getProgressByUserAndCourse(userId, courseId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProgressResponse>> getProgressByUser(@PathVariable Long userId) {
        log.info("GET /progress/user/{} - Obteniendo todos los progresos del usuario", userId);
        return ResponseEntity.ok(progressService.getProgressByUser(userId));
    }

    @PatchMapping("/{progressId}/complete")
    public ResponseEntity<ProgressResponse> completeLesson(
            @PathVariable Long progressId,
            @RequestParam Long lessonId) {
        log.info("PATCH /progress/{}/complete?lessonId={} - Marcando leccion como completada",
                progressId, lessonId);
        return ResponseEntity.ok(progressService.completeLesson(progressId, lessonId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProgressResponse> updateProgressStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        log.info("PATCH /progress/{}/status?status={} - Actualizando estado", id, status);
        return ResponseEntity.ok(progressService.updateProgressStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgress(@PathVariable Long id) {
        log.info("DELETE /progress/{} - Eliminando progreso", id);
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}