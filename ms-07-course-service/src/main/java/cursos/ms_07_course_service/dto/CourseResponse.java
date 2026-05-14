package cursos.ms_07_course_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {
    
    private Long id;
    private Long categoryId;
    private Long instructorId;
    private String title;
    private String description;
    private String imageUrl;
    private Double price;
    private boolean active;
}