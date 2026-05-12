package cursos.ms_04_user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    // ID del usuario en el auth-service
    @NotNull(message = "AuthUserId no puese ser nulo.")
    private Long authUserId;

    @NotBlank(message = "First Name no puede estar vacío.")
    private String firstName;

    @NotBlank(message = "Last Name no puede estar vacío.")
    private String lastName;

    @NotBlank(message = "Email no puede estar vacío.")
    @Email(message = "El email tiene que ser uno válido.")
    private String email;

    // Opcional, no es obligatorio al crear el perfil
    private String profilePicture;
}
