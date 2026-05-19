package cursos.ms_09_progress_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long currentLessonId;

    @Column(nullable = false)
    private Integer completedLessons;

    @Column(nullable = false)
    private Integer totalLessons;

    @Column(nullable = false)
    private Integer progressPercentage;

    @Column
    private String status;

    @Builder.Default
    @Column
    private boolean isActive = true;
}