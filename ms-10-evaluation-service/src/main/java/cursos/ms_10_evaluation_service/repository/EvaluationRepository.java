package cursos.ms_10_evaluation_service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cursos.ms_10_evaluation_service.model.entity.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByCourseId(Long courseId);

    Optional<Evaluation> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Evaluation> findByStatus(String status);
}