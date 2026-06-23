package cursos.ms_04_user_service.service;

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

import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.model.entity.UserProfile;
import cursos.ms_04_user_service.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void deberiaCrearPerfil_CuandoEmailNoExiste() {
        UserProfileRequest request = UserProfileRequest.builder()
                .authUserId(1L).firstName("Juan").lastName("Perez")
                .email("juan@duoc.cl").profilePicture("pic.jpg").build();
        when(userProfileRepository.existsByEmail("juan@duoc.cl")).thenReturn(false);
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> {
            UserProfile p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        UserProfileResponse response = userProfileService.createProfile(request);

        assertNotNull(response);
        assertEquals("juan@duoc.cl", response.getEmail());
        assertEquals("Juan", response.getFirstName());
        assertTrue(response.isActive());
        verify(userProfileRepository).existsByEmail("juan@duoc.cl");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoEmailDuplicado() {
        UserProfileRequest request = UserProfileRequest.builder()
                .authUserId(1L).firstName("J").lastName("P").email("dup@duoc.cl").build();
        when(userProfileRepository.existsByEmail("dup@duoc.cl")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userProfileService.createProfile(request));
        assertEquals("Ya existe un perfil con ese email", ex.getMessage());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void deberiaRetornarPerfil_CuandoExistePorId() {
        UserProfile profile = UserProfile.builder().id(1L).authUserId(1L).firstName("J").lastName("P").email("j@d.cl").isActive(true).build();
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = userProfileService.getProfileById(1L);

        assertNotNull(response);
        assertEquals("J", response.getFirstName());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoPerfilNoExistePorId() {
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userProfileService.getProfileById(99L));
    }

    @Test
    void deberiaRetornarTodosLosPerfiles() {
        when(userProfileRepository.findAll()).thenReturn(List.of(
                UserProfile.builder().id(1L).firstName("A").email("a@d.cl").isActive(true).build(),
                UserProfile.builder().id(2L).firstName("B").email("b@d.cl").isActive(false).build()));

        List<UserProfileResponse> result = userProfileService.getAllProfiles();

        assertEquals(2, result.size());
    }

    @Test
    void deberiaRetornarPerfil_CuandoExistePorAuthUserId() {
        UserProfile profile = UserProfile.builder().id(1L).authUserId(10L).firstName("J").email("j@d.cl").build();
        when(userProfileRepository.findByAuthUserId(10L)).thenReturn(Optional.of(profile));

        UserProfileResponse response = userProfileService.getProfileByAuthUserID(10L);

        assertNotNull(response);
        assertEquals(10L, response.getAuthUserId());
    }

    @Test
    void deberiaRetornarPerfil_CuandoExistePorEmail() {
        UserProfile profile = UserProfile.builder().id(1L).email("j@d.cl").firstName("J").build();
        when(userProfileRepository.findByEmail("j@d.cl")).thenReturn(Optional.of(profile));

        UserProfileResponse response = userProfileService.getProfileByEmail("j@d.cl");

        assertEquals("j@d.cl", response.getEmail());
    }

    @Test
    void deberiaActualizarPerfil() {
        UserProfile existing = UserProfile.builder().id(1L).firstName("Viejo").lastName("N").email("j@d.cl").isActive(true).build();
        UserProfileRequest request = UserProfileRequest.builder()
                .authUserId(1L).firstName("Nuevo").lastName("Apellido").profilePicture("new.jpg").build();
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));

        UserProfileResponse response = userProfileService.updateProfile(1L, request);

        assertEquals("Nuevo", response.getFirstName());
        assertEquals("Apellido", response.getLastName());
    }

    @Test
    void deberiaRealizarSoftDelete() {
        UserProfile profile = UserProfile.builder().id(1L).firstName("J").email("j@d.cl").isActive(true).build();
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(profile));

        userProfileService.deleteProfile(1L);

        assertFalse(profile.isActive());
        verify(userProfileRepository).save(profile);
    }

    @Test
    void deberiaLanzarExcepcion_CuandoDeleteNoExiste() {
        when(userProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userProfileService.deleteProfile(99L));
    }
}
