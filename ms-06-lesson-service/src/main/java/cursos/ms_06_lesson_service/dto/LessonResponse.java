package cursos.ms_06_lesson_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Integer durationMinutes;
    private boolean active;
}