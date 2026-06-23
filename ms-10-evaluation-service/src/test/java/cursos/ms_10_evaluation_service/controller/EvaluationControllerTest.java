package cursos.ms_10_evaluation_service.controller;

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

import cursos.ms_10_evaluation_service.dto.EvaluationRequest;
import cursos.ms_10_evaluation_service.dto.EvaluationResponse;
import cursos.ms_10_evaluation_service.service.EvaluationService;

@WebMvcTest(EvaluationController.class)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationService evaluationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCrearEvaluacion() throws Exception {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Examen Final").description("X").maxScore(100).passingScore(70).build();
        EvaluationResponse response = EvaluationResponse.builder()
                .id(1L).title("Examen Final").maxScore(100).passingScore(70).build();
        when(evaluationService.createEvaluation(any(EvaluationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Examen Final"));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        EvaluationRequest request = EvaluationRequest.builder().build();

        mockMvc.perform(post("/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorId() throws Exception {
        when(evaluationService.getEvaluationById(1L))
                .thenReturn(EvaluationResponse.builder().id(1L).title("Examen").build());

        mockMvc.perform(get("/evaluations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Examen"));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorCurso() throws Exception {
        when(evaluationService.getEvaluationsByCourseId(1L)).thenReturn(List.of(
                EvaluationResponse.builder().id(1L).title("A").build()));

        mockMvc.perform(get("/evaluations/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodas() throws Exception {
        when(evaluationService.getAllEvaluations()).thenReturn(List.of(
                EvaluationResponse.builder().id(1L).title("A").build()));

        mockMvc.perform(get("/evaluations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoActualizar() throws Exception {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Actualizado").description("Y").build();
        when(evaluationService.updateEvaluation(any(Long.class), any(EvaluationRequest.class)))
                .thenReturn(EvaluationResponse.builder().id(1L).title("Actualizado").build());

        mockMvc.perform(put("/evaluations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Actualizado"));
    }

    @Test
    void deberiaRetornar204_CuandoEliminar() throws Exception {
        doNothing().when(evaluationService).deleteEvaluation(1L);

        mockMvc.perform(delete("/evaluations/1"))
                .andExpect(status().isNoContent());
    }
}
