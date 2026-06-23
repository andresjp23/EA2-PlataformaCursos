package cursos.ms_06_lesson_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cursos.ms_06_lesson_service.dto.LessonRequest;
import cursos.ms_06_lesson_service.dto.LessonResponse;
import cursos.ms_06_lesson_service.service.LessonService;

@WebMvcTest(LessonController.class)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCrearLeccion() throws Exception {
        LessonRequest request = LessonRequest.builder()
                .courseId(1L).title("Intro").content("Contenido").build();
        LessonResponse response = LessonResponse.builder()
                .id(1L).courseId(1L).title("Intro").content("Contenido").active(true).build();
        when(lessonService.createLesson(any(LessonRequest.class))).thenReturn(response);

        mockMvc.perform(post("/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Intro"));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        LessonRequest request = LessonRequest.builder().build();

        mockMvc.perform(post("/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerLeccionPorId() throws Exception {
        LessonResponse response = LessonResponse.builder()
                .id(1L).courseId(1L).title("Intro").content("X").active(true).build();
        when(lessonService.getLessonById(1L)).thenReturn(response);

        mockMvc.perform(get("/lessons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Intro"));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerLeccionesPorCurso() throws Exception {
        when(lessonService.getLessonsByCourseId(1L)).thenReturn(List.of(
                LessonResponse.builder().id(1L).courseId(1L).title("A").content("X").active(true).build(),
                LessonResponse.builder().id(2L).courseId(1L).title("B").content("Y").active(true).build()));

        mockMvc.perform(get("/lessons/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodasLasLecciones() throws Exception {
        when(lessonService.getAllLessons()).thenReturn(List.of(
                LessonResponse.builder().id(1L).courseId(1L).title("A").content("X").active(true).build()));

        mockMvc.perform(get("/lessons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoActualizarLeccion() throws Exception {
        LessonRequest request = LessonRequest.builder()
                .courseId(1L).title("Updated").content("New").build();
        LessonResponse response = LessonResponse.builder()
                .id(1L).courseId(1L).title("Updated").content("New").active(true).build();
        when(lessonService.updateLesson(any(Long.class), any(LessonRequest.class))).thenReturn(response);

        mockMvc.perform(put("/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deberiaRetornar204_CuandoEliminarLeccion() throws Exception {
        doNothing().when(lessonService).deleteLesson(1L);

        mockMvc.perform(delete("/lessons/1"))
                .andExpect(status().isNoContent());
    }
}
