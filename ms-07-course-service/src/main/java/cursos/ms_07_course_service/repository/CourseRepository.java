package cursos.ms_07_course_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cursos.ms_07_course_service.model.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // Busca un curso por su titulo
    Optional<Course> findByTitle (String title);

    // Verifica si ya existe un curso con el mismo titulo
    boolean existsByTitle (String title);

    // Verifica si hay cursos en la categoria
    boolean existsByCategoryId (Long categoryId);

}
