package cursos.ms_11_certificate_service.controller;

import static org.mockito.ArgumentMatchers.any;
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

import cursos.ms_11_certificate_service.dto.CertificateRequest;
import cursos.ms_11_certificate_service.dto.CertificateResponse;
import cursos.ms_11_certificate_service.service.CertificateService;

@WebMvcTest(CertificateController.class)
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CertificateService certificateService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    void deberiaRetornar201_CuandoGeneraCertificado() throws Exception {
        CertificateRequest request = CertificateRequest.builder()
                .userId(1L).courseId(1L).build();

        CertificateResponse response = CertificateResponse.builder()
                .id(1L).userId(1L).studentName("Juan Perez")
                .courseId(1L).courseTitle("Java Fundamentals")
                .finalGrade(85.0).issuedAt(LocalDateTime.now())
                .certificateCode("CERT-ABCD1234").active(true)
                .build();

        when(certificateService.generateCertificate(any(CertificateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.certificateCode").value("CERT-ABCD1234"))
                .andExpect(jsonPath("$.studentName").value("Juan Perez"));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorId() throws Exception {
        CertificateResponse response = CertificateResponse.builder()
                .id(1L).userId(1L).studentName("Juan Perez")
                .courseId(1L).courseTitle("Java Fundamentals")
                .finalGrade(85.0).issuedAt(LocalDateTime.now())
                .certificateCode("CERT-ABCD1234").active(true)
                .build();

        when(certificateService.getCertificateById(1L)).thenReturn(response);

        mockMvc.perform(get("/certificates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorCodigo() throws Exception {
        CertificateResponse response = CertificateResponse.builder()
                .certificateCode("CERT-ABCD1234").build();

        when(certificateService.getCertificateByCode("CERT-ABCD1234")).thenReturn(response);

        mockMvc.perform(get("/certificates/code/CERT-ABCD1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificateCode").value("CERT-ABCD1234"));
    }

    @Test
    void deberiaRetornar200_CuandoObtienePorUsuario() throws Exception {
        mockMvc.perform(get("/certificates/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar204_CuandoRevoca() throws Exception {
        mockMvc.perform(delete("/certificates/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        CertificateRequest request = CertificateRequest.builder().build();

        mockMvc.perform(post("/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
