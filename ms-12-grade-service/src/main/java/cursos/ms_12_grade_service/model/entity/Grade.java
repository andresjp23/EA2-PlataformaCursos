package cursos.ms_12_grade_service.model.entity;

import java.time.LocalDateTime;

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
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del estudiante que rindio la evaluacion
    @Column(nullable = false)
    private Long studentId;

    // ID de la evaluacion que rindio
    @Column(nullable = false)
    private Long evaluationId;

    // Nota obtenida (0-100)
    @Column(nullable = false)
    private Integer score;

    // Fecha y hora cuando rindio la evaluacion
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime takenAt = LocalDateTime.now();

    // Evita duplicados: un estudiante no puede rendir la misma evaluacion dos veces
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;
}