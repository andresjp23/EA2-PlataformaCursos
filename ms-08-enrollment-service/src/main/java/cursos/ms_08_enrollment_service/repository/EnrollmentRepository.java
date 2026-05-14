package cursos.ms_08_enrollment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cursos.ms_08_enrollment_service.model.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}