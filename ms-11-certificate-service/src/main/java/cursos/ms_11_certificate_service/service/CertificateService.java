package cursos.ms_11_certificate_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CourseClient courseClient;
    private final EvaluationClient evaluationClient;
    private final GradeClient gradeClient;
    private final ProgressClient progressClient;
    private final UserClient userClient;

    // Genera un nuevo certificado
    public CertificateResponse generateCertificate(CertificateRequest request) {
        log.info("Solicitud de certificado para userId: {} y courseId: {}",
                request.getUserId(), request.getCourseId());

        // 1. Verificar si ya existe un certificado activo
        if (certificateRepository.existsByUserIdAndCourseIdAndIsActiveTrue(
                request.getUserId(), request.getCourseId())) {
            log.warn("El usuario {} ya tiene un certificado activo para el curso {}",
                    request.getUserId(), request.getCourseId());
            throw new RuntimeException("El usuario ya tiene un certificado para este curso.");
        }

        // 2. Obtener datos del estudiante (nombre completo)
        UserProfileResponse user = getUserProfile(request.getUserId());

        // 3. Obtener titulo del curso
        CourseResponse course = getCourse(request.getCourseId());

        // 4. Verificar progreso completo (100%)
        verifyProgressComplete(request.getUserId(), request.getCourseId());

        // 5. Obtener y verificar evaluacion aprobada
        Double finalGrade = getAndVerifyApprovedGrade(request.getUserId(), request.getCourseId());

        // 6. Generar codigo unico para el certificado
        String certificateCode = generateUniqueCode();

        // 7. Crear y guardar el certificado
        Certificate certificate = Certificate.builder()
                .userId(request.getUserId())
                .studentName(user.getFirstName() + " " + user.getLastName())
                .courseId(request.getCourseId())
                .courseTitle(course.getTitle())
                .finalGrade(finalGrade)
                .issuedAt(LocalDateTime.now())
                .certificateCode(certificateCode)
                .isActive(true)
                .build();

        Certificate saved = certificateRepository.save(certificate);
        log.info("Certificado generado exitosamente con ID: {} y codigo: {}",
                saved.getId(), saved.getCertificateCode());

        return toResponse(saved);
    }

    // Obtener certificado por ID
    public CertificateResponse getCertificateById(Long id) {
        log.info("Buscando certificado con ID: {}", id);

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Certificado no encontrado: {}", id);
                    return new RuntimeException("Certificado no encontrado.");
                });

        return toResponse(certificate);
    }

    // Obtener certificado por codigo unico
    public CertificateResponse getCertificateByCode(String code) {
        log.info("Buscando certificado por codigo: {}", code);

        Certificate certificate = certificateRepository.findByCertificateCodeAndIsActiveTrue(code)
                .orElseThrow(() -> {
                    log.warn("Certificado no encontrado con codigo: {}", code);
                    return new RuntimeException("Certificado no encontrado.");
                });

        return toResponse(certificate);
    }

    // Listar certificados de un usuario
    public List<CertificateResponse> getCertificatesByUser(Long userId) {
        log.info("Obteniendo certificados del usuario: {}", userId);

        return certificateRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Revocar un certificado (soft delete)
    public void revokeCertificate(Long id) {
        log.info("Revocando certificado con ID: {}", id);

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Certificado no encontrado para revocar: {}", id);
                    return new RuntimeException("Certificado no encontrado.");
                });

        certificate.setActive(false);
        certificateRepository.save(certificate);

        log.info("Certificado revocado exitosamente con ID: {}", id);
    }

    // === METODOS AUXILIARES FEIGN ===

    private UserProfileResponse getUserProfile(Long userId) {
        try {
            UserProfileResponse user = userClient.getUserById(userId);
            log.info("Datos del estudiante obtenidos: {} {}", user.getFirstName(), user.getLastName());
            return user;
        } catch (Exception e) {
            log.error("Error al obtener datos del estudiante {}: {}", userId, e.getMessage());
            throw new RuntimeException("No se pudo obtener la informacion del estudiante.");
        }
    }

    private CourseResponse getCourse(Long courseId) {
        try {
            CourseResponse course = courseClient.getCourseById(courseId);
            log.info("Curso {} validado: {}", courseId, course.getTitle());
            return course;
        } catch (Exception e) {
            log.error("Error al obtener curso {}: {}", courseId, e.getMessage());
            throw new RuntimeException("No se encontro el curso especificado.");
        }
    }

    private void verifyProgressComplete(Long userId, Long courseId) {
        try {
            ProgressResponse progress = progressClient.getProgressByUserAndCourse(userId, courseId);
            if (progress.getProgressPercentage() < 100) {
                log.warn("El estudiante {} no ha completado el 100% del curso {}. Progreso: {}%",
                        userId, courseId, progress.getProgressPercentage());
                throw new RuntimeException("El estudiante no ha completado el 100% del curso.");
            }
            if (!"COMPLETED".equals(progress.getStatus())) {
                log.warn("El progreso del estudiante {} no esta en estado COMPLETED. Estado: {}",
                        userId, progress.getStatus());
                throw new RuntimeException("El progreso del estudiante no esta en estado completado.");
            }
            log.info("Progreso verificado: 100% completado, estado COMPLETED");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al verificar progreso {}: {}", courseId, e.getMessage());
            throw new RuntimeException("No se pudo verificar el progreso del estudiante.");
        }
    }

    private Double getAndVerifyApprovedGrade(Long userId, Long courseId) {
        try {
            // 1. Obtener las evaluaciones del curso para saber el passingScore
            List<EvaluationResponse> evaluations = evaluationClient.getEvaluationsByCourse(courseId);
            if (evaluations == null || evaluations.isEmpty()) {
                throw new RuntimeException("No se encontro evaluacion para este curso.");
            }
            EvaluationResponse evaluation = evaluations.get(0);
            log.info("Evaluacion encontrada para curso {}: passingScore = {}",
                    courseId, evaluation.getPassingScore());

            // 2. Obtener la nota del estudiante desde grade-service
            GradeResponse grade = gradeClient.getGradeByStudentAndEvaluation(userId, evaluation.getId());
            log.info("Nota del estudiante {} en evaluacion {}: {}",
                    userId, evaluation.getId(), grade.getScore());

            // 3. Verificar que la nota es mayor o igual al passingScore
            if (grade.getScore() < evaluation.getPassingScore()) {
                log.warn("El estudiante {} no aprobo. Nota: {}, passingScore: {}",
                        userId, grade.getScore(), evaluation.getPassingScore());
                throw new RuntimeException("El estudiante no aprobo la evaluacion. Nota insuficiente.");
            }

            log.info("Evaluacion aprobada. Nota: {} >= {}", grade.getScore(), evaluation.getPassingScore());
            return grade.getScore().doubleValue();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener nota para userId {} y courseId {}: {}", userId, courseId, e.getMessage());
            throw new RuntimeException("No se pudo obtener o verificar la nota del estudiante.");
        }
    }

    private String generateUniqueCode() {
        return "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private CertificateResponse toResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .userId(certificate.getUserId())
                .studentName(certificate.getStudentName())
                .courseId(certificate.getCourseId())
                .courseTitle(certificate.getCourseTitle())
                .finalGrade(certificate.getFinalGrade())
                .issuedAt(certificate.getIssuedAt())
                .certificateCode(certificate.getCertificateCode())
                .active(certificate.isActive())
                .build();
    }
}