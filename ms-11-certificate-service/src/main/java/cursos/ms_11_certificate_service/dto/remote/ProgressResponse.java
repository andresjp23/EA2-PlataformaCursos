package cursos.ms_11_certificate_service.dto.remote;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressResponse {
    private Long id;
    private Long userId;
    private Long courseId;
    private Integer progressPercentage;
    private String status;
    private boolean active;
}