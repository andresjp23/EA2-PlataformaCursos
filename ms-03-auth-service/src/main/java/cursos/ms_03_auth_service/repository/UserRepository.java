package cursos.ms_03_auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import cursos.ms_03_auth_service.model.entity.User;



public interface UserRepository extends JpaRepository<User, Long>{

    // Busca un usuario por su username
    // Spring Data JPA genera la consulta automaticamente por el nombre del metodo
    Optional<User> findByUsername(String username);

    // Verifica si ya existe un usuario con ese username (cuando uno se registre verficamos que no se repitan)
    boolean existsByUsername(String username);

    // Verifica si ya existe un usuario con ese email (nos sirve igualmente para el registro)
    boolean existsByEmail(String email);



}
