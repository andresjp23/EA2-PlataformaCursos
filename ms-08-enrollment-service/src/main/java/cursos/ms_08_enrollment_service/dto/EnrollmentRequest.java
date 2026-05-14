package cursos.ms_08_enrollment_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequest {
    @NotNull(message = "UserId no puede ser nulo.")
    private Long userId;

    @NotNull(message = "CourseId no puede ser nulo.")
    private Long courseId;
}