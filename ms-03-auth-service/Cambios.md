---
1.3 CREAR MANEJO CENTRALIZADO DE EXCEPCIONES
Paso 1.3.1: Crear ErrorResponse en auth-service
Crear nuevo archivo: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-03-auth-service/src/main/java/cursos/ms_03_auth_service/exception/ErrorResponse.java
Código completo:
package cursos.ms_03_auth_service.exception;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}
---
Paso 1.3.2: Crear GlobalExceptionHandler en auth-service
Crear nuevo archivo: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-03-auth-service/src/main/java/cursos/ms_03_auth_service/exception/GlobalExceptionHandler.java
Código completo:
package cursos.ms_03_auth_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja excepciones de validación (Bean Validation)
     * Se dispara cuando @Valid falla en un @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        // Extraemos cada error de validación de campo
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.add(ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
        );
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validación fallida para los datos enviados")
                .errors(fieldErrors)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Maneja RuntimeException genéricas (errores de lógica de negocio)
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Maneja cualquier otra excepción no prevista
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado en el servidor")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
Notas importantes:
- Esta clase maneja TODAS las excepciones de auth-service
- @ControllerAdvice es a nivel de aplicación completa
- Cada @ExceptionHandler maneja un tipo específico de excepción
- El orden importa: las más específicas primero, las genéricas al final
---
Paso 1.3.3: Crear ErrorResponse en user-service
Crear nuevo archivo: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-04-user-service/src/main/java/cursos/ms_04_user_service/exception/ErrorResponse.java
Código exacto (mismo que auth-service):
package cursos.ms_04_user_service.exception;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> errors;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}
---
Paso 1.3.4: Crear GlobalExceptionHandler en user-service
Crear nuevo archivo: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-04-user-service/src/main/java/cursos/ms_04_user_service/exception/GlobalExceptionHandler.java
Código exacto (mismo que auth-service):
package cursos.ms_04_user_service.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja excepciones de validación (Bean Validation)
     * Se dispara cuando @Valid falla en un @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        // Extraemos cada error de validación de campo
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.add(ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
        );
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validación fallida para los datos enviados")
                .errors(fieldErrors)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Maneja RuntimeException genéricas (errores de lógica de negocio)
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    /**
     * Maneja cualquier otra excepción no prevista
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado en el servidor")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
---
1.4 AGREGAR LOGGING CON SLF4J Y LOMBOK
Paso 1.4.1: Agregar @Slf4j a AuthController
Ubicación: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-03-auth-service/src/main/java/cursos/ms_03_auth_service/controller/AuthController.java
Código exacto a reemplazar:
package cursos.ms_03_auth_service.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import cursos.ms_03_auth_service.dto.AuthResponse;
import cursos.ms_03_auth_service.dto.LoginRequest;
import cursos.ms_03_auth_service.dto.RegisterRequest;
import cursos.ms_03_auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    // Endpoint publico para que los estudiantes se registren solos
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }
    // Endpoint publico para que cualquier usuario inicie sesion (haga login)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
Por esto:
package cursos.ms_03_auth_service.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import cursos.ms_03_auth_service.dto.AuthResponse;
import cursos.ms_03_auth_service.dto.LoginRequest;
import cursos.ms_03_auth_service.dto.RegisterRequest;
import cursos.ms_03_auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    // Endpoint publico para que los estudiantes se registren solos
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        log.info("Iniciando proceso de registro para usuario: {}", request.getUsername());
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.register(request));
        log.info("Usuario registrado exitosamente: {}", request.getUsername());
        return response;
    }
    // Endpoint publico para que cualquier usuario inicie sesion (haga login)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        log.info("Iniciando proceso de login para usuario: {}", request.getUsername());
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.login(request));
        log.info("Usuario logueado exitosamente: {}", request.getUsername());
        return response;
    }
}
Cambios:
- Línea 13: import lombok.extern.slf4j.Slf4j;
- Línea 15: @Slf4j anotación
- Línea 28-31: Logs en register()
- Línea 36-39: Logs en login()
---
Paso 1.4.2: Agregar @Slf4j a AuthService
Ubicación: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-03-auth-service/src/main/java/cursos/ms_03_auth_service/service/AuthService.java
Código exacto a reemplazar:
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    public AuthResponse register(RegisterRequest request) {
        // Verificamos que el username y email no estén en uso
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya está en uso");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }
        // Buscamos el rol STUDENT porque el registro público es solo para estudiantes
        Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Rol STUDENT no encontrado"));
        // Creamos el usuario con la contraseña encriptada
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        // Guardamos el usuario en la BD
        userRepository.save(user);
        // Generamos y devolvemos el token JWT
        String token = jwtService.generateToken(user.getUsername());
        return AuthResponse.builder().token(token).build();
    }
    public AuthResponse login(LoginRequest request) {
        // Buscamos el usuario en la BD
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Verificamos que la contraseña sea correcta
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        // Generamos y devolvemos el token JWT
        String token = jwtService.generateToken(user.getUsername());
        return AuthResponse.builder().token(token).build();
    }
}
Por esto:
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
                .orElseThrow(() -> {
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
        log.info("Usuario registrado exitosamente: {}", request.getUsername());
        // Generamos y devolvemos el token JWT
        String token = jwtService.generateToken(user.getUsername());
        log.debug("Token JWT generado para usuario: {}", request.getUsername());
        return AuthResponse.builder().token(token).build();
    }
    public AuthResponse login(LoginRequest request) {
        log.info("Iniciando login para usuario: {}", request.getUsername());
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
        String token = jwtService.generateToken(user.getUsername());
        log.info("Login exitoso para usuario: {}", request.getUsername());
        log.debug("Token JWT generado para usuario: {}", request.getUsername());
        return AuthResponse.builder().token(token).build();
    }
}
Cambios:
- Línea 11: import lombok.extern.slf4j.Slf4j;
- Línea 14: @Slf4j anotación
- Línea 26: Log info al iniciar registro
- Línea 30: Log warn si username duplicado
- Línea 34: Log warn si email duplicado
- Línea 40-44: Log error si rol no encontrado
- Línea 54: Log info después de guardar
- Línea 58: Log debug cuando genera token
- Línea 63: Log info al iniciar login
- Línea 67-71: Log warn si usuario no encontrado
- Línea 75-77: Log warn si contraseña incorrecta
- Línea 82-83: Logs info y debug en login exitoso
---
Paso 1.4.3: Agregar @Slf4j a UserProfileController
Ubicación: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-04-user-service/src/main/java/cursos/ms_04_user_service/controller/UserProfileController.java
Código exacto a reemplazar:
package cursos.ms_04_user_service.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    // Crear un nuevo perfil de usuario
    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(@Valid @RequestBody UserProfileRequest request){
        return ResponseEntity.ok(userProfileService.createProfile(request));
    }
    // Obtener perfil por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfileByID(@PathVariable Long id){
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }
    // Obtener perfil por ID del usuario en el auth-service
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserProfileResponse> getProfileByAuthUserId(@PathVariable Long authUserId){
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserID(authUserId));
    }
    
    // Obtener perfil por email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponse> getProfileByEmail(@PathVariable String email){
        return ResponseEntity.ok(userProfileService.getProfileByEmail(email));
    }
    // Actualizar perfil
    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(Long id, @Valid @RequestBody UserProfileRequest request){
        return ResponseEntity.ok(userProfileService.updateProfile(id, request));
    }
    // Eliminar perfil (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        userProfileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}
Por esto:
package cursos.ms_04_user_service.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;
    // Crear un nuevo perfil de usuario
    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(@Valid @RequestBody UserProfileRequest request){
        log.info("Iniciando creación de perfil para email: {}", request.getEmail());
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userProfileService.createProfile(request));
        log.info("Perfil creado exitosamente para email: {}", request.getEmail());
        return response;
    }
    // Obtener perfil por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfileByID(@PathVariable Long id){
        log.info("Obteniendo perfil con ID: {}", id);
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }
    // Obtener perfil por ID del usuario en el auth-service
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserProfileResponse> getProfileByAuthUserId(@PathVariable Long authUserId){
        log.info("Obteniendo perfil con authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserID(authUserId));
    }
    
    // Obtener perfil por email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponse> getProfileByEmail(@PathVariable String email){
        log.info("Obteniendo perfil con email: {}", email);
        return ResponseEntity.ok(userProfileService.getProfileByEmail(email));
    }
    // Actualizar perfil
    @PutMapping("/update")
    public ResponseEntity<UserProfileResponse> updateProfile(Long id, @Valid @RequestBody UserProfileRequest request){
        log.info("Iniciando actualización de perfil con ID: {}", id);
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userProfileService.updateProfile(id, request));
        log.info("Perfil actualizado exitosamente con ID: {}", id);
        return response;
    }
    // Eliminar perfil (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        log.info("Iniciando eliminación (soft delete) de perfil con ID: {}", id);
        userProfileService.deleteProfile(id);
        log.info("Perfil eliminado exitosamente (soft delete) con ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
Cambios:
- Línea 17: import lombok.extern.slf4j.Slf4j;
- Línea 19: @Slf4j anotación
- Línea 30-33: Logs en createProfile()
- Línea 37-39: Logs en getProfileByID()
- Línea 43-45: Logs en getProfileByAuthUserId()
- Línea 50-52: Logs en getProfileByEmail()
- Línea 56-60: Logs en updateProfile()
- Línea 64-68: Logs en deleteProfile()
---
Paso 1.4.4: Agregar @Slf4j a UserProfileService
Ubicación: /Users/ajordanp/MyProjects/DuocUc/FULLSTACK/EA2-PlataformaCursos/ms-04-user-service/src/main/java/cursos/ms_04_user_service/service/UserProfileService.java
Código exacto a reemplazar:
package cursos.ms_04_user_service.service;
import org.springframework.stereotype.Service;
import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.model.entity.UserProfile;
import cursos.ms_04_user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    // Crea un nuevo perfil de usuario
    public UserProfileResponse createProfile(UserProfileRequest request){
        
        // Verificamos que no exista ya un perfil con ese email
        if (userProfileRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Ya existe un perfil con ese email");
        }
        // Construimos el perfil con los datos del request
        UserProfile profile = UserProfile.builder()
                    .authUserId(request.getAuthUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .profilePicture(request.getProfilePicture())
                    .build();
        // Guardamos en la BD
        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(saved);
    }
    // Obtiene un perfil por su ID
    public UserProfileResponse getProfileById(Long id){
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));
        return toResponse(profile);
    }
    // Obtiene un perfil por el ID del usuario en el auth-service
    public UserProfileResponse getProfileByAuthUserID(Long authUserId){
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));
        return toResponse(profile);
    }
    // Obtiene un perfil por su email
    public UserProfileResponse getProfileByEmail(String email){
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));
        return toResponse(profile);
    }
    // Actualiza los datos de un perfil existente
    public UserProfileResponse updateProfile(Long id, UserProfileRequest request){
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado con ese id."));
        // Actualizamos los datos de un perfil existente
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setProfilePicture(request.getProfilePicture());
        UserProfile  updated = userProfileRepository.save(profile);
        return toResponse(updated); 
    }
    // Soft delete: no borra el registro, solo lo marca como inactivo
    public void deleteProfile(Long id) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));
        // En vez de borrar el perfil de usuario, cambiamos isActive a false. 
        profile.setActive(false);
        userProfileRepository.save(profile);
    }
    // Convierte la entidad al DTO de respuesta
    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .profilePicture(profile.getProfilePicture())
                .active(profile.isActive())
                .build();
    }
}
Por esto:
package cursos.ms_04_user_service.service;
import org.springframework.stereotype.Service;
import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.model.entity.UserProfile;
import cursos.ms_04_user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    // Crea un nuevo perfil de usuario
    public UserProfileResponse createProfile(UserProfileRequest request){
        log.info("Creando nuevo perfil para authUserId: {}", request.getAuthUserId());
        
        // Verificamos que no exista ya un perfil con ese email
        if (userProfileRepository.existsByEmail(request.getEmail())){
            log.warn("Intento de crear perfil con email duplicado: {}", request.getEmail());
            throw new RuntimeException("Ya existe un perfil con ese email");
        }
        // Construimos el perfil con los datos del request
        UserProfile profile = UserProfile.builder()
                    .authUserId(request.getAuthUserId())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .profilePicture(request.getProfilePicture())
                    .build();
        // Guardamos en la BD
        UserProfile saved = userProfileRepository.save(profile);
        log.info("Perfil creado exitosamente con ID: {}", saved.getId());
        return toResponse(saved);
    }
    // Obtiene un perfil por su ID
    public UserProfileResponse getProfileById(Long id){
        log.info("Buscando perfil con ID: {}", id);
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado con ID: {}", id);
                    return new RuntimeException("Perfil no encontrado.");
                });
        log.debug("Perfil encontrado con ID: {}", id);
        return toResponse(profile);
    }
    // Obtiene un perfil por el ID del usuario en el auth-service
    public UserProfileResponse getProfileByAuthUserID(Long authUserId){
        log.info("Buscando perfil con authUserId: {}", authUserId);
        UserProfile profile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado con authUserId: {}", authUserId);
                    return new RuntimeException("Perfil no encontrado.");
                });
        log.debug("Perfil encontrado con authUserId: {}", authUserId);
        return toResponse(profile);
    }
    // Obtiene un perfil por su email
    public UserProfileResponse getProfileByEmail(String email){
        log.info("Buscando perfil con email: {}", email);
        UserProfile profile = userProfileRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado con email: {}", email);
                    return new RuntimeException("Perfil no encontrado.");
                });
        log.debug("Perfil encontrado con email: {}", email);
        return toResponse(profile);
    }
    // Actualiza los datos de un perfil existente
    public UserProfileResponse updateProfile(Long id, UserProfileRequest request){
        log.info("Actualizando perfil con ID: {}", id);
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado para actualizar con ID: {}", id);
                    return new RuntimeException("Perfil no encontrado con ese id.");
                });
        // Actualizamos los datos de un perfil existente
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setProfilePicture(request.getProfilePicture());
        UserProfile  updated = userProfileRepository.save(profile);
        log.info("Perfil actualizado exitosamente con ID: {}", id);
        return toResponse(updated); 
    }
    // Soft delete: no borra el registro, solo lo marca como inactivo
    public void deleteProfile(Long id) {
        log.info("Eliminando (soft delete) perfil con ID: {}", id);
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado para eliminar con ID: {}", id);
                    return new RuntimeException("Perfil no encontrado.");
                });
        // En vez de borrar el perfil de usuario, cambiamos isActive a false. 
        profile.setActive(false);
        userProfileRepository.save(profile);
        log.info("Perfil eliminado exitosamente (soft delete) con ID: {}", id);
    }
    // Convierte la entidad al DTO de respuesta
    private UserProfileResponse toResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .profilePicture(profile.getProfilePicture())
                .active(profile.isActive())
                .build();
    }
}
Cambios:
- Línea 11: import lombok.extern.slf4j.Slf4j;
- Línea 13: @Slf4j anotación
- Línea 22: Log info al crear
- Línea 26: Log warn si email duplicado
- Línea 40: Log info después de guardar
- Línea 46-51: Logs info, warn, debug en getProfileById()
- Línea 56-61: Logs info, warn, debug en getProfileByAuthUserID()
- Línea 66-71: Logs info, warn, debug en getProfileByEmail()
- Línea 76-84: Logs info, warn en updateProfile()
- Línea 86: Log info después de actualizar
- Línea 92-99: Logs info, warn en deleteProfile()
- Línea 105: Log info después de eliminar (soft delete)
---
RESUMEN VISUAL DE CAMBIOS - FASE 1
Archivos a MODIFICAR (cambiar código existente):
1. ✅ ms-03-auth-service/pom.xml - Agregar dependency
2. ✅ ms-04-user-service/pom.xml - Agregar dependency
3. ✅ ms-03-auth-service/dto/LoginRequest.java - Agregar validaciones
4. ✅ ms-03-auth-service/dto/RegisterRequest.java - Agregar validaciones
5. ✅ ms-04-user-service/dto/UserProfileRequest.java - Agregar validaciones
6. ✅ ms-03-auth-service/controller/AuthController.java - Agregar @Valid y logs
7. ✅ ms-04-user-service/controller/UserProfileController.java - Agregar @Valid y logs
8. ✅ ms-03-auth-service/service/AuthService.java - Agregar logs
9. ✅ ms-04-user-service/service/UserProfileService.java - Agregar logs
Archivos a CREAR (nuevos):
1. ✅ ms-03-auth-service/exception/ErrorResponse.java - DTO para errores
2. ✅ ms-03-auth-service/exception/GlobalExceptionHandler.java - Manejo centralizado
3. ✅ ms-04-user-service/exception/ErrorResponse.java - DTO para errores
4. ✅ ms-04-user-service/exception/GlobalExceptionHandler.java - Manejo centralizado
Total: 9 modificaciones + 4 archivos nuevos = 13 cambios
---
NOTAS IMPORTANTES
1. Validación activa: @Valid en los controllers disparará automáticamente el GlobalExceptionHandler si hay errores
2. Logs niveles: 
   - log.info() → eventos normales importantes
   - log.warn() → advertencias/errores esperados
   - log.debug() → información de debugging detallada
3. ErrorResponse: Se devuelve automáticamente cuando hay errores (gracias a @ControllerAdvice)
4. Lombok: Lombok ya viene en pom.xml, así que @Slf4j funcionará automáticamente
¿Deseas que proceda con la implementación de FASE 1 ahora, o tienes preguntas sobre algún paso específico?
▣  Plan · Claude Haiku 4.5 · 1m 33s