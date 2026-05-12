package cursos.ms_03_auth_service.service;

import cursos.ms_03_auth_service.dto.AuthResponse;
import cursos.ms_03_auth_service.dto.LoginRequest;
import cursos.ms_03_auth_service.dto.RegisterRequest;
import cursos.ms_03_auth_service.model.entity.Role;
import cursos.ms_03_auth_service.model.entity.User;
import cursos.ms_03_auth_service.repository.RoleRepository;
import cursos.ms_03_auth_service.repository.UserRepository;
import cursos.ms_03_auth_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro para usuario: {}", request.getUsername());

        // Verificamos que el username y email no estén en uso
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Intento de registro con username duplicado: {}", request.getUsername());
            throw new RuntimeException("El username ya está en uso");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Intento de registro con email duplicado: {}", request.getEmail());
            throw new RuntimeException("El email ya está en uso");
        }

        // Buscamos el rol STUDENT porque el registro público es solo para estudiantes
        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() ->{
                    log.error("Rol STUDENT no encontrado en la base de datos");
                    return new RuntimeException("Rol STUDENT no encontrado");
                });

        // Creamos el usuario con la contraseña encriptada
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        // Guardamos el usuario en la BD
        userRepository.save(user);
        log.info("Usuario registrado existosamente: {}", request.getUsername());

        // Generamos y devolvemos el token JWT
        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        log.debug("Token JWT generado para usuario: {}", request.getUsername());
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Iniciando login para el usuario: {}", request.getUsername());

        // Buscamos el usuario en la BD
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Intento de login con usuario no encontrado: {}", request.getUsername());
                    return new RuntimeException("Usuario no encontrado");
                });

        // Verificamos que la contraseña sea correcta
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Intento de login con contraseña incorrecta para usuario: {}", request.getUsername());
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generamos y devolvemos el token JWT
        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        log.info("Login exitoso para usuario: {}", request.getUsername());
        log.debug("Token JWT generado para usuario: {}", request.getUsername());
        return AuthResponse.builder()
                    .token(token)
                    .role(user.getRole().getName())
                    .build();
    }
}



