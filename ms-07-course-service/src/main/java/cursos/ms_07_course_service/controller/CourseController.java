package cursos.ms_07_course_service.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cursos.ms_07_course_service.dto.CourseRequest;
import cursos.ms_07_course_service.dto.CourseResponse;
import cursos.ms_07_course_service.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // POST /courses - Crear nuevo curso
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        log.info("POST /courses -> Creando curso: {}", request.getTitle());
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /courses - Listar todos los cursos activos
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        log.info("GET /courses -> Obteniendo todos los cursos");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // GET /courses/{id} - Obtener curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        log.info("GET /courses/{} - Obteniendo curso por ID", id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // GET /courses/category/{categoryId} - Obtener cursos por categoría
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable Long categoryId) {
        log.info("GET /courses/category/{} - Obteniendo cursos por categoría", categoryId);
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId));
    }

    // GET /courses/instructor/{instructorId} - Obtener cursos por instructor
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@PathVariable Long instructorId) {
        log.info("GET /courses/instructor/{} - Obteniendo cursos por instructor", instructorId);
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    // PUT /courses/{id} - Actualizar curso
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        log.info("PUT /courses/{} - Actualizando curso", id);
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    // DELETE /courses/{id} - Eliminar (soft delete) curso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        log.info("DELETE /courses/{} - Eliminando curso", id);
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
    
    // PATCH /courses/{id}/enable - Reactivar curso
    @PatchMapping("/{id}/enable")
    public ResponseEntity<CourseResponse> enableCourse(@PathVariable Long id) {
        log.info("PATCH /courses/{}/enable - Reactivando curso", id);
        return ResponseEntity.ok(courseService.enableCourse(id));
    }
}