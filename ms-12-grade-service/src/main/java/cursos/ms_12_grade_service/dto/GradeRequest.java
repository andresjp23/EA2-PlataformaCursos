package cursos.ms_12_grade_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequest {

    @NotNull(message = "El ID del estudiante no puede ser nulo.")
    private Long studentId;

    @NotNull(message = "El ID de la evaluacion no puede ser nulo.")
    private Long evaluationId;

    @NotNull(message = "La nota no puede ser nula.")
    @Min(value = 0, message = "La nota minima es 0.")
    @Max(value = 100, message = "La nota maxima es 100.")
    private Integer score;
}