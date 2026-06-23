package cursos.ms_07_course_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cursos.ms_07_course_service.dto.CourseRequest;
import cursos.ms_07_course_service.dto.CourseResponse;
import cursos.ms_07_course_service.model.entity.Course;
import cursos.ms_07_course_service.repository.CourseRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void deberiaCrearCurso_CuandoTituloNoExiste() {
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Java 101")
                .description("Curso de Java basico").price(99.99).build();
        when(courseRepository.existsByTitle("Java 101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> {
            Course c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        CourseResponse response = courseService.createCourse(request);

        assertNotNull(response);
        assertEquals("Java 101", response.getTitle());
        assertTrue(response.isActive());
        assertEquals(99.99, response.getPrice());
        verify(courseRepository).existsByTitle("Java 101");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoTituloDuplicado() {
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Dupe").description("X").build();
        when(courseRepository.existsByTitle("Dupe")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> courseService.createCourse(request));
        assertEquals("Ya existe un curso con ese título.", ex.getMessage());
        verify(courseRepository, never()).save(any());
    }

    @Test
    void deberiaRetornarCurso_CuandoExiste() {
        Course course = Course.builder().id(1L).title("Java").description("X").isActive(true).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponse response = courseService.getCourseById(1L);

        assertEquals("Java", response.getTitle());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoCursoNoExiste() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getCourseById(99L));
    }

    @Test
    void deberiaRetornarSoloActivos() {
        when(courseRepository.findAll()).thenReturn(List.of(
                Course.builder().id(1L).title("A").isActive(true).build(),
                Course.builder().id(2L).title("B").isActive(false).build()));

        List<CourseResponse> result = courseService.getAllCourses();

        assertEquals(1, result.size());
    }

    @Test
    void deberiaRetornarCursosPorCategoria() {
        when(courseRepository.findAll()).thenReturn(List.of(
                Course.builder().id(1L).title("A").categoryId(1L).isActive(true).build(),
                Course.builder().id(2L).title("B").categoryId(2L).isActive(true).build()));

        List<CourseResponse> result = courseService.getCoursesByCategory(1L);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getTitle());
    }

    @Test
    void deberiaRetornarCursosPorInstructor() {
        when(courseRepository.findAll()).thenReturn(List.of(
                Course.builder().id(1L).title("A").instructorId(1L).isActive(true).build(),
                Course.builder().id(2L).title("B").instructorId(2L).isActive(true).build()));

        List<CourseResponse> result = courseService.getCoursesByInstructor(1L);

        assertEquals(1, result.size());
    }

    @Test
    void deberiaActualizarCurso_SinDuplicado() {
        Course existing = Course.builder().id(1L).title("Viejo").description("X").categoryId(1L).instructorId(1L).isActive(true).build();
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Nuevo")
                .description("Nueva desc").price(49.99).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.existsByTitle("Nuevo")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        CourseResponse response = courseService.updateCourse(1L, request);

        assertEquals("Nuevo", response.getTitle());
        assertEquals(49.99, response.getPrice());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoUpdateTituloDuplicado() {
        Course existing = Course.builder().id(1L).title("Viejo").build();
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Nuevo").description("X").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.existsByTitle("Nuevo")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseService.updateCourse(1L, request));
    }

    @Test
    void deberiaRealizarSoftDelete() {
        Course course = Course.builder().id(1L).title("X").description("Y").isActive(true).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        assertFalse(course.isActive());
        verify(courseRepository).save(course);
    }

    @Test
    void deberiaReactivarCurso() {
        Course course = Course.builder().id(1L).title("X").description("Y").isActive(false).build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        CourseResponse response = courseService.enableCourse(1L);

        assertTrue(response.isActive());
    }
}
