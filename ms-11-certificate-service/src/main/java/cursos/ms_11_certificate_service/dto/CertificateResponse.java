package cursos.ms_11_certificate_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {

    private Long id;
    private Long userId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private Double finalGrade;
    private LocalDateTime issuedAt;
    private String certificateCode;
    private boolean active;
}