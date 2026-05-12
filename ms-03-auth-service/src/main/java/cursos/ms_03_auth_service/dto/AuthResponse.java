package cursos.ms_03_auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    // Esto es lo que devuelve nuestra API despues de un login o registro exitoso
    // El cliente guarda este token y lo manda en las requests siguientes
    private String token;
    private String role;

}
