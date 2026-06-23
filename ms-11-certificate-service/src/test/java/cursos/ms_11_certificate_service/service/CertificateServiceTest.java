package cursos.ms_11_certificate_service.service;

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

import cursos.ms_11_certificate_service.client.CourseClient;
import cursos.ms_11_certificate_service.client.EvaluationClient;
import cursos.ms_11_certificate_service.client.GradeClient;
import cursos.ms_11_certificate_service.client.ProgressClient;
import cursos.ms_11_certificate_service.client.UserClient;
import cursos.ms_11_certificate_service.dto.CertificateRequest;
import cursos.ms_11_certificate_service.dto.CertificateResponse;
import cursos.ms_11_certificate_service.dto.remote.CourseResponse;
import cursos.ms_11_certificate_service.dto.remote.EvaluationResponse;
import cursos.ms_11_certificate_service.dto.remote.GradeResponse;
import cursos.ms_11_certificate_service.dto.remote.ProgressResponse;
import cursos.ms_11_certificate_service.dto.remote.UserProfileResponse;
import cursos.ms_11_certificate_service.model.entity.Certificate;
import cursos.ms_11_certificate_service.repository.CertificateRepository;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock
    private CertificateRepository certificateRepository;

    @Mock
    private CourseClient courseClient;

    @Mock
    private EvaluationClient evaluationClient;

    @Mock
    private GradeClient gradeClient;

    @Mock
    private ProgressClient progressClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private CertificateService certificateService;

    private CertificateRequest request;
    private UserProfileResponse userProfile;
    private CourseResponse course;
    private ProgressResponse progressComplete;
    private EvaluationResponse evaluation;
    private GradeResponse gradeApproved;
    private Certificate certificate;

    @BeforeEach
    void setUp() {
        request = CertificateRequest.builder()
                .userId(1L).courseId(1L).build();

        userProfile = UserProfileResponse.builder()
                .id(1L).firstName("Juan").lastName("Perez").active(true).build();

        course = CourseResponse.builder()
                .id(1L).title("Java Fundamentals").active(true).build();

        progressComplete = ProgressResponse.builder()
                .id(1L).userId(1L).courseId(1L)
                .progressPercentage(100).status("COMPLETED").active(true).build();

        evaluation = EvaluationResponse.builder()
                .id(1L).courseId(1L)
                .title("Examen Final").passingScore(70).status("PUBLISHED").active(true).build();

        gradeApproved = GradeResponse.builder()
                .id(1L).studentId(1L).evaluationId(1L)
                .score(85).takenAt(LocalDateTime.now()).active(true).build();

        certificate = Certificate.builder()
                .id(1L).userId(1L).studentName("Juan Perez")
                .courseId(1L).courseTitle("Java Fundamentals")
                .finalGrade(85.0).issuedAt(LocalDateTime.now())
                .certificateCode("CERT-ABCD1234").isActive(true)
                .build();
    }

    @Test
    void deberiaGenerarCertificado_CuandoTodoValido() {
        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(userClient.getUserById(1L)).thenReturn(userProfile);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(progressClient.getProgressByUserAndCourse(1L, 1L)).thenReturn(progressComplete);
        when(evaluationClient.getEvaluationsByCourse(1L)).thenReturn(List.of(evaluation));
        when(gradeClient.getGradeByStudentAndEvaluation(1L, 1L)).thenReturn(gradeApproved);
        when(certificateRepository.save(any(Certificate.class))).thenReturn(certificate);

        CertificateResponse response = certificateService.generateCertificate(request);

        assertNotNull(response);
        assertEquals("Juan Perez", response.getStudentName());
        assertEquals("Java Fundamentals", response.getCourseTitle());
        assertEquals(85.0, response.getFinalGrade());
        assertTrue(response.getCertificateCode().startsWith("CERT-"));
        assertTrue(response.isActive());
        verify(certificateRepository).save(any(Certificate.class));
    }

    @Test
    void deberiaLanzarExcepcion_SiYaTieneCertificadoActivo() {
        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> certificateService.generateCertificate(request));

        assertEquals("El usuario ya tiene un certificado para este curso.", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiProgresoNoEs100() {
        ProgressResponse progressIncomplete = ProgressResponse.builder()
                .progressPercentage(50).status("ACTIVE").build();

        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(userClient.getUserById(1L)).thenReturn(userProfile);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(progressClient.getProgressByUserAndCourse(1L, 1L)).thenReturn(progressIncomplete);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> certificateService.generateCertificate(request));

        assertEquals("El estudiante no ha completado el 100% del curso.", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiProgresoNoEstaCompletado() {
        ProgressResponse progressNotCompleted = ProgressResponse.builder()
                .progressPercentage(100).status("ACTIVE").build();

        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(userClient.getUserById(1L)).thenReturn(userProfile);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(progressClient.getProgressByUserAndCourse(1L, 1L)).thenReturn(progressNotCompleted);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> certificateService.generateCertificate(request));

        assertEquals("El progreso del estudiante no esta en estado completado.", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiNoHayEvaluacion() {
        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(userClient.getUserById(1L)).thenReturn(userProfile);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(progressClient.getProgressByUserAndCourse(1L, 1L)).thenReturn(progressComplete);
        when(evaluationClient.getEvaluationsByCourse(1L)).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> certificateService.generateCertificate(request));

        assertEquals("No se encontro evaluacion para este curso.", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_SiNotaNoAlcanzaPassingScore() {
        GradeResponse gradeLow = GradeResponse.builder()
                .id(1L).studentId(1L).evaluationId(1L)
                .score(50).takenAt(LocalDateTime.now()).active(true).build();

        when(certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(userClient.getUserById(1L)).thenReturn(userProfile);
        when(courseClient.getCourseById(1L)).thenReturn(course);
        when(progressClient.getProgressByUserAndCourse(1L, 1L)).thenReturn(progressComplete);
        when(evaluationClient.getEvaluationsByCourse(1L)).thenReturn(List.of(evaluation));
        when(gradeClient.getGradeByStudentAndEvaluation(1L, 1L)).thenReturn(gradeLow);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> certificateService.generateCertificate(request));

        assertEquals("El estudiante no aprobo la evaluacion. Nota insuficiente.", exception.getMessage());
        verify(certificateRepository, never()).save(any());
    }

    @Test
    void deberiaObtenerCertificadoPorId() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));

        CertificateResponse response = certificateService.getCertificateById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Juan Perez", response.getStudentName());
    }

    @Test
    void deberiaObtenerCertificadoPorCodigo() {
        when(certificateRepository.findByCertificateCodeAndIsActiveTrue("CERT-ABCD1234"))
                .thenReturn(Optional.of(certificate));

        CertificateResponse response = certificateService.getCertificateByCode("CERT-ABCD1234");

        assertNotNull(response);
        assertEquals("CERT-ABCD1234", response.getCertificateCode());
    }

    @Test
    void deberiaRevocarCertificado() {
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(certificate));

        certificateService.revokeCertificate(1L);

        assertFalse(certificate.isActive());
        verify(certificateRepository).save(certificate);
    }
}
