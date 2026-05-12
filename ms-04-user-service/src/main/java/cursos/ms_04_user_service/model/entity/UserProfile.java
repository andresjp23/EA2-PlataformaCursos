package cursos.ms_04_user_service.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ID del usuario en el auth-service, para relacionar ambos servicios
    @Column(unique = true, nullable = false)
    private Long authUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // Email duplicado para no tener que consultar el auth-service cada vez
    @Column(unique = true, nullable = false)
    private String email;

    // Foto de perfil, guardamos las URL de la imagen
    @Column
    private String profilePicture;

    // true = activo, false = inactivo (soft delete)
    @Builder.Default
    @Column
    private boolean isActive = true;

}
