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

import cursos.ms_04_user_service.dto.UserProfileRequest;
import cursos.ms_04_user_service.dto.UserProfileResponse;
import cursos.ms_04_user_service.service.UserProfileService;
import jakarta.validation.Valid;
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
        log.info("Iniciando creacion de perfil para email: {}", request.getEmail());
        ResponseEntity<UserProfileResponse> response =  ResponseEntity.ok(userProfileService.createProfile(request));
        log.info("Perfil creado exitosamente para email: {}", request.getEmail());
        return response;
    }

    // Obtener perfil por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfileByID(@PathVariable Long id){
        log.info("Obteniendo perfil con id: {}", id);
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
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long id, @Valid @RequestBody UserProfileRequest request){
        log.info("Iniciando actualizacion de perfil con ID: {}", id);
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userProfileService.updateProfile(id, request));
        log.info("Perfil actualizado exitosamente con ID: {}", id);
        return response;
    }

    // Eliminar perfil (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        log.info("Iniciando eliminacion (soft delete) de perdil con ID: {}", id);
        userProfileService.deleteProfile(id);
        log.info("Perfil eliminado exitosamente (soft delete) con ID: {}", id);
        return ResponseEntity.noContent().build();
    }

}
