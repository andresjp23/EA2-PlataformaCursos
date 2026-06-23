package cursos.ms_12_grade_service.controller;

import static org.mockito.ArgumentMatchers.any;
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

import cursos.ms_12_grade_service.dto.GradeRequest;
import cursos.ms_12_grade_service.dto.GradeResponse;
import cursos.ms_12_grade_service.service.GradeService;

@WebMvcTest(GradeController.class)
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GradeService gradeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCrearNota() throws Exception {
        GradeRequest request = GradeRequest.builder()
                .studentId(1L).evaluationId(1L).score(85).build();
        GradeResponse response = GradeResponse.builder()
                .id(1L).studentId(1L).evaluationId(1L).score(85).build();
        when(gradeService.createGrade(any(GradeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(85));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        GradeRequest request = GradeRequest.builder().build();

        mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar400_CuandoScoreFueraDeRango() throws Exception {
        GradeRequest request = GradeRequest.builder()
                .studentId(1L).evaluationId(1L).score(150).build();

        mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorId() throws Exception {
        when(gradeService.getGradeById(1L))
                .thenReturn(GradeResponse.builder().id(1L).score(90).build());

        mockMvc.perform(get("/grades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(90));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorStudentYEvaluation() throws Exception {
        when(gradeService.getGradeByStudentAndEvaluation(1L, 1L))
                .thenReturn(GradeResponse.builder().id(1L).studentId(1L).evaluationId(1L).score(75).build());

        mockMvc.perform(get("/grades/student/1/evaluation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(75));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerNotasPorEstudiante() throws Exception {
        when(gradeService.getGradesByStudent(1L)).thenReturn(List.of(
                GradeResponse.builder().id(1L).score(80).build(),
                GradeResponse.builder().id(2L).score(90).build()));

        mockMvc.perform(get("/grades/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
