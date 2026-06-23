package cursos.ms_03_auth_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import cursos.ms_03_auth_service.dto.AuthResponse;
import cursos.ms_03_auth_service.dto.LoginRequest;
import cursos.ms_03_auth_service.dto.RegisterRequest;
import cursos.ms_03_auth_service.model.entity.Role;
import cursos.ms_03_auth_service.model.entity.User;
import cursos.ms_03_auth_service.repository.RoleRepository;
import cursos.ms_03_auth_service.repository.UserRepository;
import cursos.ms_03_auth_service.security.jwt.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Role studentRole;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .username("juanperez")
                .email("juan@duoc.cl")
                .password("password123")
                .build();

        loginRequest = LoginRequest.builder()
                .username("juanperez")
                .password("password123")
                .build();

        studentRole = Role.builder()
                .id(1L)
                .name("STUDENT")
                .build();

        user = User.builder()
                .id(1L)
                .username("juanperez")
                .email("juan@duoc.cl")
                .password("encodedPassword")
                .role(studentRole)
                .build();
    }

    @Test
    void deberiaRegistrarUsuario_CuandoDatosValidos() {
        when(userRepository.existsByUsername("juanperez")).thenReturn(false);
        when(userRepository.existsByEmail("juan@duoc.cl")).thenReturn(false);
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(studentRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken("juanperez", "STUDENT")).thenReturn("jwt-token-123");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deberiaLanzarExcepcion_SiUsernameYaExiste() {
        when(userRepository.existsByUsername("juanperez")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("El username ya está en uso", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiEmailYaExiste() {
        when(userRepository.existsByUsername("juanperez")).thenReturn(false);
        when(userRepository.existsByEmail("juan@duoc.cl")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("El email ya está en uso", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiRolStudentNoExiste() {
        when(userRepository.existsByUsername("juanperez")).thenReturn(false);
        when(userRepository.existsByEmail("juan@duoc.cl")).thenReturn(false);
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Rol STUDENT no encontrado", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deberiaLoguearUsuario_CuandoCredencialesValidas() {
        when(userRepository.findByUsername("juanperez")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken("juanperez", "STUDENT")).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void deberiaLanzarExcepcion_SiUsuarioNoExiste() {
        when(userRepository.findByUsername("juanperez")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void deberiaLanzarExcepcion_SiPasswordIncorrecto() {
        when(userRepository.findByUsername("juanperez")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("Contraseña incorrecta", exception.getMessage());
    }
}
