package cursos.ms_11_certificate_service.model.entity;

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
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID del usuario que recibio el certificado
    @Column(nullable = false)
    private Long userId;

    // Nombre completo del estudiante
    @Column(nullable = false)
    private String studentName;

    // ID del curso completado
    @Column(nullable = false)
    private Long courseId;

    // Titulo del curso
    @Column(nullable = false)
    private String courseTitle;

    // Nota final obtenida
    @Column(nullable = false)
    private Double finalGrade;

    // Fecha de emision del certificado
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    // Codigo unico para verificar autenticidad
    @Column(unique = true, nullable = false)
    private String certificateCode;

    // Para soft delete
    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;
}