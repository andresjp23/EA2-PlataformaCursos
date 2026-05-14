package cursos.ms_04_user_service.service;

import java.util.List;

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
            log.warn("Intento de creacion de perdilcon email duplicado: {}", request.getEmail());
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
        log.info("Buscando perfil con id: {}", id);
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Perfil no encontrado con ID: {}", id);
                    return new RuntimeException("Perfil no encontrado.");
                });
        log.debug("Perfil encontrado con ID: {}", id);
        return toResponse(profile);
    }

    // Listar todos los perfiles activos
    public List<UserProfileResponse> getAllProfiles (){
        log.info("Buscando todos los perfiles");
        return userProfileRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtiene un perfil por el ID del usuario en el auth-service
    public UserProfileResponse getProfileByAuthUserID(Long authUserId){
        log.info("Buscando perfil con authUserId: {}",authUserId );
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
