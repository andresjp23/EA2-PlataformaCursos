package cursos.ms_04_user_service.controller;

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

import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.service.UserProfileService;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar200_CuandoCrearPerfil() throws Exception {
        UserProfileRequest request = UserProfileRequest.builder()
                .authUserId(1L).firstName("Juan").lastName("Perez").email("j@duoc.cl").build();
        UserProfileResponse response = UserProfileResponse.builder()
                .id(1L).email("j@duoc.cl").firstName("Juan").active(true).build();
        when(userProfileService.createProfile(any(UserProfileRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("j@duoc.cl"));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        UserProfileRequest request = UserProfileRequest.builder().build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodos() throws Exception {
        when(userProfileService.getAllProfiles()).thenReturn(List.of(
                UserProfileResponse.builder().id(1L).email("a@d.cl").build()));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorId() throws Exception {
        when(userProfileService.getProfileById(1L)).thenReturn(
                UserProfileResponse.builder().id(1L).email("j@d.cl").build());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorAuthUserId() throws Exception {
        when(userProfileService.getProfileByAuthUserID(10L)).thenReturn(
                UserProfileResponse.builder().id(1L).authUserId(10L).build());

        mockMvc.perform(get("/users/auth/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authUserId").value(10));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorEmail() throws Exception {
        when(userProfileService.getProfileByEmail("j@d.cl")).thenReturn(
                UserProfileResponse.builder().id(1L).email("j@d.cl").build());

        mockMvc.perform(get("/users/email/j@d.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("j@d.cl"));
    }

    @Test
    void deberiaRetornar200_CuandoActualizarPerfil() throws Exception {
        UserProfileRequest request = UserProfileRequest.builder()
                .authUserId(1L).firstName("Nuevo").lastName("N").email("j@d.cl").build();
        when(userProfileService.updateProfile(any(Long.class), any(UserProfileRequest.class)))
                .thenReturn(UserProfileResponse.builder().id(1L).firstName("Nuevo").build());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nuevo"));
    }

    @Test
    void deberiaRetornar204_CuandoEliminarPerfil() throws Exception {
        doNothing().when(userProfileService).deleteProfile(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
