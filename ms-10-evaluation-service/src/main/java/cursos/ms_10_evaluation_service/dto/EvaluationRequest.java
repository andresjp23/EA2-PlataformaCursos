package cursos.ms_10_evaluation_service.dto;

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
public class EvaluationRequest {

    @NotNull(message = "El ID del curso no puede ser nulo.")
    private Long courseId;

    @NotBlank(message = "El titulo no puede estar vacio.")
    private String title;

    private String description;

    private Integer maxScore;

    private Integer passingScore;
}