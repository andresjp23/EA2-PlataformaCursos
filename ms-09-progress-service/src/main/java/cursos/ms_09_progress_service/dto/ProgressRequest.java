package cursos.ms_09_progress_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressRequest {

    @NotNull(message = "El ID del usuario no puede ser nulo.")
    private Long userId;

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Long courseId;

    @NotNull(message = "El ID de la leccion no puede ser nulo.")
    private Long currentLessonId;
}