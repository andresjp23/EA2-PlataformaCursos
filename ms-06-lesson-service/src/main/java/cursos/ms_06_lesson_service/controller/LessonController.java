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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(@Valid @RequestBody LessonRequest request) {
        log.info("POST /lessons -> Creando lección: {}", request.getTitle());
        LessonResponse response = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        log.info("GET /lessons/{} - Obteniendo lección por ID", id);
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByCourseId(@PathVariable Long courseId) {
        log.info("GET /lessons/course/{} - Obteniendo lecciones por curso", courseId);
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }
    @GetMapping
    public ResponseEntity<List<LessonResponse>> getAllLessons() {
        log.info("GET /lessons -> Obteniendo todas las lecciones");
        return ResponseEntity.ok(lessonService.getAllLessons());
    }
    @PutMapping("/{id}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        log.info("PUT /lessons/{} - Actualizando lección", id);
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        log.info("DELETE /lessons/{} - Eliminando lección", id);
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
