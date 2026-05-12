package cursos.ms_03_auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Username no puede estar vacío.")
    private String username;

    @NotBlank(message = "Email no puede estar vacío.")
    @Email(message = "El email deber ser uno válido.")
    private String email;

    @NotBlank(message = "Password no puede estar vacía.")
    private String password;

}
