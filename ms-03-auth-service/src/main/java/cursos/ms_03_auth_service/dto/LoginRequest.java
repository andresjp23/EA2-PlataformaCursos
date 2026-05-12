package cursos.ms_03_auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    // SOlo necesitamos el username y la password para iniciar sesion (hacer login)
    @NotBlank(message = "Username no puede estar vacío.")
    private String username;

    @NotBlank(message = "Password no puede estar vacía.")
    private String password;

}
