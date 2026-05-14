package cursos.ms_07_course_service.service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import cursos.ms_07_course_service.dto.CourseRequest;
import cursos.ms_07_course_service.dto.CourseResponse;
import cursos.ms_07_course_service.model.entity.Course;
import cursos.ms_07_course_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {


    private final CourseRepository courseRepository;

    // CREATE - Crear un nuevo curso
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creando curso con título: {}", request.getTitle());
        // Verificar título único
        if (courseRepository.existsByTitle(request.getTitle())) {
            log.warn("Intento de crear curso con título duplicado: {}", request.getTitle());
            throw new RuntimeException("Ya existe un curso con ese título.");
        }
        // Construir entidad y guardar
        Course course = Course.builder()
                .categoryId(request.getCategoryId())
                .instructorId(request.getInstructorId())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .isActive(true)
                .build();
        Course saved = courseRepository.save(course);
        log.info("Curso creado exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }

    // READ ONE - Obtener curso por ID
    public CourseResponse getCourseById(Long id) {
        log.info("Buscando curso con ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Curso no encontrado: {}", id);
                    return new RuntimeException("Curso no encontrado.");
                });
        return toResponse(course);
    }

    // READ ALL - Obtener todos los cursos activos
    public List<CourseResponse> getAllCourses() {
        log.info("Obteniendo todos los cursos activos");
        return courseRepository.findAll().stream()
                .filter(Course::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ BY CATEGORY - Obtener cursos por categoría
    public List<CourseResponse> getCoursesByCategory(Long categoryId) {
        log.info("Obteniendo cursos de la categoría: {}", categoryId);
        return courseRepository.findAll().stream()
                .filter(c -> c.isActive() && c.getCategoryId().equals(categoryId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ BY INSTRUCTOR - Obtener cursos por instructor
    public List<CourseResponse> getCoursesByInstructor(Long instructorId) {
        log.info("Obteniendo cursos del instructor: {}", instructorId);
        return courseRepository.findAll().stream()
                .filter(c -> c.isActive() && c.getInstructorId().equals(instructorId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE - Actualizar curso existente
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        log.info("Actualizando curso con ID: {}", id);
        // Buscar curso existente
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Curso no encontrado para actualizar: {}", id);
                    return new RuntimeException("Curso no encontrado.");
                });
        // Verificar título duplicado si cambió
        if (!course.getTitle().equals(request.getTitle())) {
            if (courseRepository.existsByTitle(request.getTitle())) {
                log.warn("Intento de actualizar a título duplicado: {}", request.getTitle());
                throw new RuntimeException("Ya existe un curso con ese título.");
            }
        }
        // Actualizar campos
        course.setCategoryId(request.getCategoryId());
        course.setInstructorId(request.getInstructorId());
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setImageUrl(request.getImageUrl());
        course.setPrice(request.getPrice());
        Course updated = courseRepository.save(course);
        log.info("Curso actualizado exitosamente con ID: {}", id);
        return toResponse(updated);
    }

    // DELETE (SOFT) - Marcar curso como inactivo
    public void deleteCourse(Long id) {
        log.info("Eliminando (soft delete) curso con ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Curso no encontrado para eliminar: {}", id);
                    return new RuntimeException("Curso no encontrado.");
                });
        course.setActive(false);
        courseRepository.save(course);
        log.info("Curso eliminado (soft delete) exitosamente con ID: {}", id);
    }

    // ENABLE - Reactivar curso
    public CourseResponse enableCourse(Long id) {
        log.info("Reactivando curso con ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Curso no encontrado para reactivar: {}", id);
                    return new RuntimeException("Curso no encontrado.");
                });
        course.setActive(true);
        Course enabled = courseRepository.save(course);
        log.info("Curso reactivado exitosamente con ID: {}", id);
        return toResponse(enabled);
    }
    
    // Converter: Entidad → DTO Response
    private CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .categoryId(course.getCategoryId())
                .instructorId(course.getInstructorId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .price(course.getPrice())
                .active(course.isActive())
                .build();
    }
}