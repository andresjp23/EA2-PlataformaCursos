package cursos.ms_11_certificate_service.dto.remote;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResponse {
    private Long id;
    private Long courseId;
    private String title;
    private Integer passingScore;
    private String status;
    private boolean active;
}