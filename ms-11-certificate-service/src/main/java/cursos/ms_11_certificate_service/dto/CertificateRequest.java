package cursos.ms_11_certificate_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateRequest {

    @NotNull(message = "El ID del usuario no puede ser nulo.")
    private Long userId;

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Long courseId;
}