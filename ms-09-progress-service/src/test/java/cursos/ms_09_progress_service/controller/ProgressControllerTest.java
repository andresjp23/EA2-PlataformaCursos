package cursos.ms_09_progress_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cursos.ms_09_progress_service.dto.ProgressRequest;
import cursos.ms_09_progress_service.dto.ProgressResponse;
import cursos.ms_09_progress_service.service.ProgressService;

@WebMvcTest(ProgressController.class)
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressService progressService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCreaProgreso() throws Exception {
        ProgressRequest request = ProgressRequest.builder()
                .userId(1L).courseId(1L).currentLessonId(1L).build();

        ProgressResponse response = ProgressResponse.builder()
                .id(1L).userId(1L).courseId(1L).currentLessonId(1L)
                .completedLessons(0).totalLessons(10).progressPercentage(0)
                .status("ACTIVE").active(true)
                .build();

        when(progressService.createProgress(any(ProgressRequest.class))).thenReturn(response);

        mockMvc.perform(post("/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorId() throws Exception {
        mockMvc.perform(get("/progress/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorUsuarioYCurso() throws Exception {
        mockMvc.perform(get("/progress/user/1/course/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar200_CuandoCompletaLeccion() throws Exception {
        mockMvc.perform(patch("/progress/1/complete?lessonId=2"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar204_CuandoElimina() throws Exception {
        mockMvc.perform(delete("/progress/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        ProgressRequest request = ProgressRequest.builder().build();

        mockMvc.perform(post("/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
