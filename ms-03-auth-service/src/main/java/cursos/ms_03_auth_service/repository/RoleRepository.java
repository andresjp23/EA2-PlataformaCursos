package cursos.ms_03_auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cursos.ms_03_auth_service.model.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Busca un rol por su nombre, ej: "ADMIN", "TEACHER", "STUDENT"
    Optional<Role> findByName(String name);
}
