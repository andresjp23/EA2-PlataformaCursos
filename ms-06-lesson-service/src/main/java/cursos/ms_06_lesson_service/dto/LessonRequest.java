package cursos.ms_06_lesson_service.dto;

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
public class LessonRequest {
    @NotNull(message = "CourseId no puede ser nulo.")
    private Long courseId;
    @NotBlank(message = "Title no puede estar vacío.")
    private String title;
    @NotBlank(message = "Content no puede estar vacío.")
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
}
