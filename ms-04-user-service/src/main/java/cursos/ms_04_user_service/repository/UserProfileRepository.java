package cursos.ms_04_user_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import cursos.ms_04_user_service.model.entity.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Busca un perfil por el ID del usuario en auth-service
    Optional<UserProfile> findByAuthUserId(Long authUserId);

    // Busca un perfil por email
    Optional<UserProfile> findByEmail(String email);

    // Verifica si ya existe un perfil con ese email
    boolean existsByEmail(String email);

}
