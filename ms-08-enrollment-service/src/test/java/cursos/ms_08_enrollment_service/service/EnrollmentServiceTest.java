package cursos.ms_08_enrollment_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cursos.ms_08_enrollment_service.client.CourseClient;
import cursos.ms_08_enrollment_service.client.UserClient;
import cursos.ms_08_enrollment_service.dto.EnrollmentRequest;
import cursos.ms_08_enrollment_service.dto.EnrollmentResponse;
import cursos.ms_08_enrollment_service.model.entity.Enrollment;
import cursos.ms_08_enrollment_service.repository.EnrollmentRepository;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseClient courseClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private EnrollmentRequest request;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        request = EnrollmentRequest.builder()
                .userId(1L)
                .courseId(1L)
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .userId(1L)
                .courseId(1L)
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE")
                .isActive(true)
                .build();
    }

    @Test
    void deberiaCrearInscripcion_CuandoCursoYUsuarioExisten() {
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenReturn(new Object());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse response = enrollmentService.createEnrollment(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getCourseId());
        assertEquals("ACTIVE", response.getStatus());
        assertTrue(response.isActive());
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoInscripcionDuplicada() {
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrollmentService.createEnrollment(request));

        assertEquals("El usuario ya está inscrito en este curso.", exception.getMessage());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoCursoNoExiste() {
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        when(courseClient.getCourseById(1L)).thenThrow(new RuntimeException("Curso no encontrado"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrollmentService.createEnrollment(request));

        assertEquals("No se encontró el curso especificado.", exception.getMessage());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoUsuarioNoExiste() {
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(userClient.getUserById(1L)).thenThrow(new RuntimeException("Usuario no encontrado"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrollmentService.createEnrollment(request));

        assertEquals("No se encontró el usuario especificado.", exception.getMessage());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void deberiaObtenerInscripcionPorId() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        EnrollmentResponse response = enrollmentService.getEnrollmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoInscripcionNoExiste() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrollmentService.getEnrollmentById(99L));

        assertEquals("Inscripción no encontrada.", exception.getMessage());
    }

    @Test
    void deberiaObtenerInscripcionesPorUsuario() {
        Enrollment otro = Enrollment.builder()
                .id(2L).userId(1L).courseId(2L)
                .enrolledAt(LocalDateTime.now()).status("ACTIVE").isActive(true)
                .build();
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment, otro));

        List<EnrollmentResponse> responses = enrollmentService.getEnrollmentsByUser(1L);

        assertEquals(2, responses.size());
    }

    @Test
    void deberiaCancelarInscripcion() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(1L);

        assertFalse(enrollment.isActive());
        assertEquals("CANCELLED", enrollment.getStatus());
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void deberiaLanzarExcepcion_CuandoCancelaInscripcionInexistente() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> enrollmentService.cancelEnrollment(99L));

        assertEquals("Inscripción no encontrada.", exception.getMessage());
        verify(enrollmentRepository, never()).save(any());
    }
}
