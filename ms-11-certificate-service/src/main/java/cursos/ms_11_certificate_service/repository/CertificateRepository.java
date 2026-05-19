package cursos.ms_11_certificate_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cursos.ms_11_certificate_service.model.entity.Certificate;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // Busca si un usuario ya tiene certificado para un curso especifico
    Optional<Certificate> findByUserIdAndCourseIdAndIsActiveTrue(Long userId, Long courseId);

    // Lista todos los certificados de un usuario
    List<Certificate> findByUserIdAndIsActiveTrue(Long userId);

    // Verifica si ya existe para evitar duplicados
    boolean existsByUserIdAndCourseIdAndIsActiveTrue(Long userId, Long courseId);

    // Busca por codigo unico de verificacion
    Optional<Certificate> findByCertificateCodeAndIsActiveTrue(String certificateCode);
}