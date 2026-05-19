package cursos.ms_11_certificate_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cursos.ms_11_certificate_service.dto.CertificateRequest;
import cursos.ms_11_certificate_service.dto.CertificateResponse;
import cursos.ms_11_certificate_service.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    // POST /certificates - Genera un nuevo certificado
    @PostMapping
    public ResponseEntity<CertificateResponse> generateCertificate(
            @Valid @RequestBody CertificateRequest request) {
        log.info("POST /certificates -> Generando certificado para user: {} course: {}",
                request.getUserId(), request.getCourseId());
        CertificateResponse response = certificateService.generateCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /certificates/{id} - Obtiene certificado por ID
    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponse> getCertificateById(@PathVariable Long id) {
        log.info("GET /certificates/{} - Obteniendo certificado por ID", id);
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    // GET /certificates/code/{code} - Obtiene certificado por codigo unico
    @GetMapping("/code/{code}")
    public ResponseEntity<CertificateResponse> getCertificateByCode(@PathVariable String code) {
        log.info("GET /certificates/code/{} - Obteniendo certificado por codigo", code);
        return ResponseEntity.ok(certificateService.getCertificateByCode(code));
    }

    // GET /certificates/user/{userId} - Lista certificados de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponse>> getCertificatesByUser(@PathVariable Long userId) {
        log.info("GET /certificates/user/{} - Obteniendo certificados del usuario", userId);
        return ResponseEntity.ok(certificateService.getCertificatesByUser(userId));
    }

    // DELETE /certificates/{id} - Revoca un certificado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeCertificate(@PathVariable Long id) {
        log.info("DELETE /certificates/{} - Revocando certificado", id);
        certificateService.revokeCertificate(id);
        return ResponseEntity.noContent().build();
    }
}