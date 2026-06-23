package cursos.ms_09_progress_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cursos.ms_09_progress_service.client.CourseClient;
import cursos.ms_09_progress_service.client.LessonClient;
import cursos.ms_09_progress_service.dto.ProgressRequest;
import cursos.ms_09_progress_service.dto.ProgressResponse;
import cursos.ms_09_progress_service.model.entity.Progress;
import cursos.ms_09_progress_service.repository.ProgressRepository;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @Mock
    private LessonClient lessonClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private ProgressService progressService;

    private ProgressRequest request;
    private Progress progress;

    @BeforeEach
    void setUp() {
        request = ProgressRequest.builder()
                .userId(1L).courseId(1L).currentLessonId(1L)
                .build();

        progress = Progress.builder()
                .id(1L).userId(1L).courseId(1L).currentLessonId(1L)
                .completedLessons(0).totalLessons(10).progressPercentage(0)
                .status("ACTIVE").isActive(true)
                .build();
    }

    @Test
    void deberiaCrearProgreso_ConLeccionesTotales() {
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(progressRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(lessonClient.getLessonsByCourseId(1L)).thenReturn(List.of(
                new Object(), new Object(), new Object(), new Object(), new Object(),
                new Object(), new Object(), new Object(), new Object(), new Object()));
        when(progressRepository.save(any(Progress.class))).thenReturn(progress);

        ProgressResponse response = progressService.createProgress(request);

        assertNotNull(response);
        assertEquals(10, response.getTotalLessons());
        assertEquals(0, response.getCompletedLessons());
        assertEquals(0, response.getProgressPercentage());
        verify(progressRepository).save(any(Progress.class));
    }

    @Test
    void deberiaLanzarExcepcion_SiCursoNoExiste() {
        when(courseClient.getCourseById(1L)).thenThrow(new RuntimeException("Curso no encontrado"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> progressService.createProgress(request));

        assertEquals("No se encontro el curso especificado.", exception.getMessage());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiProgresoYaExiste() {
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(progressRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> progressService.createProgress(request));

        assertEquals("El usuario ya tiene progreso en este curso.", exception.getMessage());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void deberiaObtenerProgresoPorId() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));

        ProgressResponse response = progressService.getProgressById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void deberiaObtenerProgresoPorUsuarioYCurso() {
        when(progressRepository.findByUserIdAndCourseIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.of(progress));

        ProgressResponse response = progressService.getProgressByUserAndCourse(1L, 1L);

        assertNotNull(response);
        assertEquals(0, response.getProgressPercentage());
    }

    @Test
    void deberiaCalcularPorcentaje_Correctamente() {
        Progress progresoActual = Progress.builder()
                .id(1L).userId(1L).courseId(1L).currentLessonId(2L)
                .completedLessons(2).totalLessons(10).progressPercentage(20)
                .status("ACTIVE").isActive(true)
                .build();

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progresoActual));
        when(progressRepository.save(any(Progress.class))).thenAnswer(i -> i.getArgument(0));

        ProgressResponse response = progressService.completeLesson(1L, 3L);

        assertEquals(3, response.getCompletedLessons());
        assertEquals(30, response.getProgressPercentage()); // 3*100/10 = 30%
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    void deberiaMarcarCompletado_CuandoTerminaTodas() {
        Progress progresoFinal = Progress.builder()
                .id(1L).userId(1L).courseId(1L).currentLessonId(9L)
                .completedLessons(9).totalLessons(10).progressPercentage(90)
                .status("ACTIVE").isActive(true)
                .build();

        when(progressRepository.findById(1L)).thenReturn(Optional.of(progresoFinal));
        when(progressRepository.save(any(Progress.class))).thenAnswer(i -> i.getArgument(0));

        ProgressResponse response = progressService.completeLesson(1L, 10L);

        assertEquals(10, response.getCompletedLessons());
        assertEquals(100, response.getProgressPercentage());
        assertEquals("COMPLETED", response.getStatus());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoProgresoNoExiste() {
        when(progressRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> progressService.completeLesson(99L, 1L));

        assertEquals("Progreso no encontrado.", exception.getMessage());
        verify(progressRepository, never()).save(any());
    }

    @Test
    void deberiaEliminarProgreso_SoftDelete() {
        when(progressRepository.findById(1L)).thenReturn(Optional.of(progress));

        progressService.deleteProgress(1L);

        assertFalse(progress.isActive());
        verify(progressRepository).save(progress);
    }
}
