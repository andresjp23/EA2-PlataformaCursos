package cursos.ms_08_enrollment_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cursos.ms_08_enrollment_service.client.CourseClient;
import cursos.ms_08_enrollment_service.client.UserClient;
import cursos.ms_08_enrollment_service.dto.EnrollmentRequest;
import cursos.ms_08_enrollment_service.dto.EnrollmentResponse;
import cursos.ms_08_enrollment_service.model.entity.Enrollment;
import cursos.ms_08_enrollment_service.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;
    private final UserClient userClient;

    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        log.info("Creando inscripción para userId: {} en courseId: {}", request.getUserId(), request.getCourseId());

        // Verificar si ya existe inscripción activa
        if (enrollmentRepository.existsByUserIdAndCourseId(request.getUserId(), request.getCourseId())) {
            log.warn("El usuario {} ya está inscrito en el curso {}", request.getUserId(), request.getCourseId());
            throw new RuntimeException("El usuario ya está inscrito en este curso.");
        }

        // Validar que existe el curso (Feign)
        try {
            courseClient.getCourseById(request.getCourseId());
            log.info("Curso {} validado correctamente", request.getCourseId());
        } catch (Exception e) {
            log.error("Error al validar curso {}: {}", request.getCourseId(), e.getMessage());
            throw new RuntimeException("No se encontró el curso especificado.");
        }

        // Validar que existe el usuario (Feign)
        try {
            userClient.getUserById(request.getUserId());
            log.info("Usuario {} validado correctamente", request.getUserId());
        } catch (Exception e) {
            log.error("Error al validar usuario {}: {}", request.getUserId(), e.getMessage());
            throw new RuntimeException("No se encontró el usuario especificado.");
        }

        // Crear inscripción
        Enrollment enrollment = Enrollment.builder()
                .userId(request.getUserId())
                .courseId(request.getCourseId())
                .enrolledAt(LocalDateTime.now())
                .status("ACTIVE")
                .isActive(true)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Inscripción creada exitosamente con ID: {}", saved.getId());

        return toResponse(saved);
    }

    public EnrollmentResponse getEnrollmentById(Long id) {
        log.info("Buscando inscripción con ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inscripción no encontrada: {}", id);
                    return new RuntimeException("Inscripción no encontrada.");
                });

        return toResponse(enrollment);
    }

    public List<EnrollmentResponse> getEnrollmentsByUser(Long userId) {
        log.info("Obteniendo inscripciones del usuario: {}", userId);

        return enrollmentRepository.findAll().stream()
                .filter(e -> e.isActive() && e.getUserId().equals(userId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        log.info("Obteniendo inscripciones del curso: {}", courseId);

        return enrollmentRepository.findAll().stream()
                .filter(e -> e.isActive() && e.getCourseId().equals(courseId))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponse> getAllEnrollments() {
        log.info("Obteniendo todas las inscripciones activas");

        return enrollmentRepository.findAll().stream()
                .filter(Enrollment::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EnrollmentResponse updateEnrollmentStatus(Long id, String status) {
        log.info("Actualizando estado de inscripción {} a: {}", id, status);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inscripción no encontrada para actualizar: {}", id);
                    return new RuntimeException("Inscripción no encontrada.");
                });

        enrollment.setStatus(status);
        Enrollment updated = enrollmentRepository.save(enrollment);
        log.info("Estado de inscripción actualizado a: {}", status);

        return toResponse(updated);
    }

    public void cancelEnrollment(Long id) {
        log.info("Cancelando inscripción con ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Inscripción no encontrada para cancelar: {}", id);
                    return new RuntimeException("Inscripción no encontrada.");
                });

        enrollment.setActive(false);
        enrollment.setStatus("CANCELLED");
        enrollmentRepository.save(enrollment);

        log.info("Inscripción cancelada exitosamente con ID: {}", id);
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .courseId(enrollment.getCourseId())
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus())
                .active(enrollment.isActive())
                .build();
    }
}