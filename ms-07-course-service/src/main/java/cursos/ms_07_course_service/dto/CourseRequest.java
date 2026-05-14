package cursos.ms_07_course_service.dto;
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
public class CourseRequest {
    @NotNull(message = "CategoryId no puede ser nulo.")
    private Long categoryId;
    @NotNull(message = "InstructorId no puede ser nulo.")
    private Long instructorId;
    @NotBlank(message = "Title no puede estar vacío.")
    private String title;
    @NotBlank(message = "Description no puede estar vacío.")
    private String description;
    private String imageUrl;
    private Double price;
}