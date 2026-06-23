package cursos.ms_08_enrollment_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cursos.ms_08_enrollment_service.dto.EnrollmentRequest;
import cursos.ms_08_enrollment_service.dto.EnrollmentResponse;
import cursos.ms_08_enrollment_service.service.EnrollmentService;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnrollmentService enrollmentService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void deberiaRetornar201_CuandoCreaInscripcion() throws Exception {
        EnrollmentRequest request = EnrollmentRequest.builder()
                .userId(1L).courseId(1L).build();

        EnrollmentResponse response = EnrollmentResponse.builder()
                .id(1L).userId(1L).courseId(1L)
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE").active(true)
                .build();

        when(enrollmentService.createEnrollment(any(EnrollmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorId() throws Exception {
        EnrollmentResponse response = EnrollmentResponse.builder()
                .id(1L).userId(1L).courseId(1L)
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE").active(true)
                .build();

        when(enrollmentService.getEnrollmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.courseId").value(1L));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorUsuario() throws Exception {
        mockMvc.perform(get("/enrollments/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorCurso() throws Exception {
        mockMvc.perform(get("/enrollments/course/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar200_CuandoObtieneTodas() throws Exception {
        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar204_CuandoCancela() throws Exception {
        mockMvc.perform(delete("/enrollments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        EnrollmentRequest request = EnrollmentRequest.builder().build();

        mockMvc.perform(post("/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
