package cursos.ms_03_auth_service.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique= true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique  = true, nullable = false)
    private String email;

    // Un usuario tiene exactamente un rol (ADMIN, TEACHER o STUDENT)
    // ManyToOne porque muchos usuarios pueden tener el mismo rol
    // pero cada usuario solo tiene uno
    @ManyToOne(fetch = FetchType.EAGER)

    // Esta anotación crea la columna "role_id" en la tabla "users"
    // que apunta al id del rol correspondiente
    @JoinColumn(name = "role_id")
    private Role role;

}
