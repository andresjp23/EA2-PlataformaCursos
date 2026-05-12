package cursos.ms_03_auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_03_auth_service.dto.AuthResponse;
import cursos.ms_03_auth_service.dto.LoginRequest;
import cursos.ms_03_auth_service.dto.RegisterRequest;
import cursos.ms_03_auth_service.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthResponse> register( @Valid @RequestBody RegisterRequest request){
        log.info("Iniciando proceso de registro para usuario: {}", request.getUsername());
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.register(request));
        log.info("Usuario registrado exitosamente: {}", request.getUsername());
        return response;
    }

    // Endpoint publico para que cualquier usuario inicie sesion (haga login)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        log.info("Iniciando proceso login para usuario: {}", request.getUsername());
        ResponseEntity<AuthResponse> response = ResponseEntity.ok(authService.login(request));
        log.info("Usuario logeado exitosamente: {}", request.getUsername());
        return response;
    }

}
