package cursos.ms_12_grade_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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