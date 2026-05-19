package cursos.ms_12_grade_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cursos.ms_12_grade_service.model.entity.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // Busca la nota de un estudiante en una evaluacion especifica
    Optional<Grade> findByStudentIdAndEvaluationIdAndIsActiveTrue(Long studentId, Long evaluationId);

    // Lista todas las notas de un estudiante
    List<Grade> findByStudentIdAndIsActiveTrue(Long studentId);

    // Verifica si ya existe una nota para ese estudiante y esa evaluacion
    boolean existsByStudentIdAndEvaluationIdAndIsActiveTrue(Long studentId, Long evaluationId);
}