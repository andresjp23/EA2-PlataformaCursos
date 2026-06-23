package cursos.ms_06_lesson_service.service;

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

import cursos.ms_06_lesson_service.dto.LessonRequest;
import cursos.ms_06_lesson_service.dto.LessonResponse;
import cursos.ms_06_lesson_service.model.entity.Lesson;
import cursos.ms_06_lesson_service.repository.LessonRepository;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @InjectMocks
    private LessonService lessonService;

    @Test
    void deberiaCrearLeccion_CuandoTituloNoExiste() {
        LessonRequest request = LessonRequest.builder()
                .courseId(1L).title("Introduccion").content("Contenido")
                .videoUrl("http://video.com").orderIndex(1).durationMinutes(10)
                .build();
        when(lessonRepository.existsByTitle("Introduccion")).thenReturn(false);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> {
            Lesson l = i.getArgument(0);
            l.setId(1L);
            return l;
        });

        LessonResponse response = lessonService.createLesson(request);

        assertNotNull(response);
        assertEquals("Introduccion", response.getTitle());
        assertEquals(1L, response.getCourseId());
        assertTrue(response.isActive());
        verify(lessonRepository).existsByTitle("Introduccion");
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoTituloDuplicado() {
        LessonRequest request = LessonRequest.builder()
                .courseId(1L).title("Intro").content("X").build();
        when(lessonRepository.existsByTitle("Intro")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonService.createLesson(request));
        assertEquals("Ya existe una leccion con ese titulo.", ex.getMessage());
        verify(lessonRepository, never()).save(any());
    }

    @Test
    void deberiaRetornarLeccion_CuandoExiste() {
        Lesson lesson = Lesson.builder().id(1L).courseId(1L).title("Intro").content("X").isActive(true).build();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        LessonResponse response = lessonService.getLessonById(1L);

        assertNotNull(response);
        assertEquals("Intro", response.getTitle());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoLeccionNoExiste() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonService.getLessonById(99L));
        assertEquals("Lección no encontrada.", ex.getMessage());
    }

    @Test
    void deberiaRetornarSoloLeccionesActivas_PorCurso() {
        Lesson active = Lesson.builder().id(1L).courseId(1L).title("A").content("X").isActive(true).build();
        Lesson inactive = Lesson.builder().id(2L).courseId(1L).title("B").content("Y").isActive(false).build();
        when(lessonRepository.findByCourseId(1L)).thenReturn(List.of(active, inactive));

        List<LessonResponse> result = lessonService.getLessonsByCourseId(1L);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getTitle());
    }

    @Test
    void deberiaRetornarSoloLeccionesActivas_Todas() {
        Lesson active1 = Lesson.builder().id(1L).courseId(1L).title("A").content("X").isActive(true).build();
        Lesson active2 = Lesson.builder().id(2L).courseId(2L).title("B").content("Y").isActive(true).build();
        Lesson inactive = Lesson.builder().id(3L).courseId(1L).title("C").content("Z").isActive(false).build();
        when(lessonRepository.findAll()).thenReturn(List.of(active1, active2, inactive));

        List<LessonResponse> result = lessonService.getAllLessons();

        assertEquals(2, result.size());
    }

    @Test
    void deberiaActualizarLeccion_CuandoExisteYsinDuplicado() {
        Lesson existing = Lesson.builder().id(1L).courseId(1L).title("Viejo").content("X").isActive(true).build();
        LessonRequest request = LessonRequest.builder()
                .courseId(1L).title("Nuevo").content("Y").orderIndex(2).durationMinutes(15).build();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.existsByTitle("Nuevo")).thenReturn(false);
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        LessonResponse response = lessonService.updateLesson(1L, request);

        assertEquals("Nuevo", response.getTitle());
        assertEquals("Y", response.getContent());
        assertEquals(Integer.valueOf(2), response.getOrderIndex());
        verify(lessonRepository).save(existing);
    }

    @Test
    void deberiaLanzarExcepcion_CuandoUpdateTituloDuplicado() {
        Lesson existing = Lesson.builder().id(1L).courseId(1L).title("Viejo").content("X").isActive(true).build();
        LessonRequest request = LessonRequest.builder().courseId(1L).title("Nuevo").content("Y").build();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.existsByTitle("Nuevo")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonService.updateLesson(1L, request));
        assertEquals("Ya existe una lección con ese título.", ex.getMessage());
    }

    @Test
    void deberiaRealizarSoftDelete_CuandoExiste() {
        Lesson lesson = Lesson.builder().id(1L).courseId(1L).title("X").content("X").isActive(true).build();
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.deleteLesson(1L);

        assertFalse(lesson.isActive());
        verify(lessonRepository).save(lesson);
    }

    @Test
    void deberiaLanzarExcepcion_CuandoDeleteNoExiste() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> lessonService.deleteLesson(99L));
        assertEquals("Lección no encontrada.", ex.getMessage());
    }
}
