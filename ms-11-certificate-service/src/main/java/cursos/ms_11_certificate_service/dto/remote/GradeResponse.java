package cursos.ms_11_certificate_service.dto.remote;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {
    private Long id;
    private Long studentId;
    private Long evaluationId;
    private Integer score;
    private LocalDateTime takenAt;
    private boolean active;
}