package cursos.ms_04_user_service.controller;

import java.util.List;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de perfiles de usuario")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "Crear perfil de usuario", description = "Crea un nuevo perfil de usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del perfil a crear", required = true, content = @Content(schema = @Schema(implementation = UserProfileRequest.class)))
            @Valid @RequestBody UserProfileRequest request){
        log.info("Iniciando creacion de perfil para email: {}", request.getEmail());
        ResponseEntity<UserProfileResponse> response =  ResponseEntity.ok(userProfileService.createProfile(request));
        log.info("Perfil creado exitosamente para email: {}", request.getEmail());
        return response;
    }

    @Operation(summary = "Obtener todos los perfiles", description = "Retorna una lista de todos los perfiles de usuario")
    @ApiResponse(responseCode = "200", description = "Lista de perfiles obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles (){
        log.info("Obteniendo todos los perfiles.");
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    @Operation(summary = "Obtener perfil por ID", description = "Busca y retorna un perfil de usuario por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfileByID(
            @Parameter(description = "ID del perfil", required = true) @PathVariable Long id){
        log.info("Obteniendo perfil con id: {}", id);
        return ResponseEntity.ok(userProfileService.getProfileById(id));
    }

    @Operation(summary = "Obtener perfil por ID de autenticación", description = "Busca un perfil usando el ID del usuario en el servicio de autenticación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/auth/{authUserId}")
    public ResponseEntity<UserProfileResponse> getProfileByAuthUserId(
            @Parameter(description = "ID de usuario en auth-service", required = true) @PathVariable Long authUserId){
        log.info("Obteniendo perfil con authUserId: {}", authUserId);
        return ResponseEntity.ok(userProfileService.getProfileByAuthUserID(authUserId));
    }

    @Operation(summary = "Obtener perfil por email", description = "Busca y retorna un perfil de usuario por su email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileResponse> getProfileByEmail(
            @Parameter(description = "Email del usuario", required = true) @PathVariable String email){
        log.info("Obteniendo perfil con email: {}", email);
        return ResponseEntity.ok(userProfileService.getProfileByEmail(email));
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos de un perfil de usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Parameter(description = "ID del perfil a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del perfil", required = true, content = @Content(schema = @Schema(implementation = UserProfileRequest.class)))
            @Valid @RequestBody UserProfileRequest request){
        log.info("Iniciando actualizacion de perfil con ID: {}", id);
        ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userProfileService.updateProfile(id, request));
        log.info("Perfil actualizado exitosamente con ID: {}", id);
        return response;
    }

    @Operation(summary = "Eliminar perfil", description = "Realiza un soft delete de un perfil de usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Perfil eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(
            @Parameter(description = "ID del perfil a eliminar", required = true) @PathVariable Long id) {
        log.info("Iniciando eliminacion (soft delete) de perdil con ID: {}", id);
        userProfileService.deleteProfile(id);
        log.info("Perfil eliminado exitosamente (soft delete) con ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}