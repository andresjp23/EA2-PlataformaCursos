package cursos.ms_09_progress_service.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cursos.ms_09_progress_service.model.entity.Progress;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Progress> findByUserIdAndCourseIdAndIsActiveTrue(Long userId, Long courseId);

    List<Progress> findByUserId(Long userId);

    List<Progress> findByCourseId(Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseIdAndIsActiveTrue(Long userId, Long courseId);
}