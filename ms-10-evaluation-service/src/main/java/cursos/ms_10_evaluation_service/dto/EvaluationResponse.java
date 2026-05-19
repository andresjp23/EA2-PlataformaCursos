package cursos.ms_10_evaluation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer maxScore;
    private Integer passingScore;
    private String status;
    private boolean active;
}