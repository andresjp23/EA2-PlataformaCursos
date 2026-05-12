package cursos.ms_05_category_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import cursos.ms_05_category_service.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Buscamos una categoria por su id
    @NonNull
    Optional<Category> findById(@NonNull Long id);

    // Verificamos si existe una categoria
    boolean existsById(@NonNull Long id);

    // Buscar una categoria por su nombre
    Optional<Category> findByName(String name);

}
