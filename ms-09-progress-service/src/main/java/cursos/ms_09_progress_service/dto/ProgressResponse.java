package cursos.ms_09_progress_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {

    private Long id;
    private Long userId;
    private Long courseId;
    private Long currentLessonId;
    private Integer completedLessons;
    private Integer totalLessons;
    private Integer progressPercentage;
    private String status;
    private boolean active;
}