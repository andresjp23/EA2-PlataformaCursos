package cursos.ms_04_user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private Long id;
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;

    // Informamos si el usuario esta activo o no
    private boolean active;

}
