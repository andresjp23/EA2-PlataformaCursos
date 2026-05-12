package cursos.ms_03_auth_service.config;

import cursos.ms_03_auth_service.repository.RoleRepository;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cursos.ms_03_auth_service.model.entity.Role;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;

    // Este método se ejecuta automáticamente al arrancar la app
    @Bean
    public CommandLineRunner initRoles() {
        return args -> {

            // Por cada rol, lo creamos solo si no existe en la BD
            for (String roleName : List.of("ADMIN", "TEACHER", "STUDENT")) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(Role.builder().name(roleName).build());
                    System.out.println("Rol creado: " + roleName);
                }
            }
        };
    }
}