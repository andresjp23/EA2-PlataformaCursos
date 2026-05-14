package cursos.ms_06_lesson_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cursos.ms_06_lesson_service.model.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long>{

    // Busca la clase por el courseId
    Optional<Lesson> findByCourseId(Long courseId);

    // Busca la clase por el nombre (titulo)
    Optional<Lesson> findByTitle (String title);

    // Verifica si ya existe una clase con ese titulo
    boolean existsByTitle (String title);
}
