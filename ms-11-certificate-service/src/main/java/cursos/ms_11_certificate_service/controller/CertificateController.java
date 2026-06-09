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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificados", description = "Gestión de certificados")
public class CertificateController {

    private final CertificateService certificateService;

    @Operation(summary = "Generar certificado", description = "Genera un nuevo certificado para un usuario que completó un curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Certificado generado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CertificateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CertificateResponse> generateCertificate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del certificado a generar", required = true, content = @Content(schema = @Schema(implementation = CertificateRequest.class)))
            @Valid @RequestBody CertificateRequest request) {
        log.info("POST /certificates -> Generando certificado para user: {} course: {}",
                request.getUserId(), request.getCourseId());
        CertificateResponse response = certificateService.generateCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener certificado por ID", description = "Busca y retorna un certificado por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Certificado encontrado"),
        @ApiResponse(responseCode = "404", description = "Certificado no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CertificateResponse> getCertificateById(
            @Parameter(description = "ID del certificado", required = true) @PathVariable Long id) {
        log.info("GET /certificates/{} - Obteniendo certificado por ID", id);
        return ResponseEntity.ok(certificateService.getCertificateById(id));
    }

    @Operation(summary = "Obtener certificado por código", description = "Busca un certificado usando su código único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Certificado encontrado"),
        @ApiResponse(responseCode = "404", description = "Certificado no encontrado")
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<CertificateResponse> getCertificateByCode(
            @Parameter(description = "Código único del certificado", required = true) @PathVariable String code) {
        log.info("GET /certificates/code/{} - Obteniendo certificado por codigo", code);
        return ResponseEntity.ok(certificateService.getCertificateByCode(code));
    }

    @Operation(summary = "Obtener certificados por usuario", description = "Retorna todos los certificados de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de certificados obtenida exitosamente")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CertificateResponse>> getCertificatesByUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long userId) {
        log.info("GET /certificates/user/{} - Obteniendo certificados del usuario", userId);
        return ResponseEntity.ok(certificateService.getCertificatesByUser(userId));
    }

    @Operation(summary = "Revocar certificado", description = "Revoca un certificado del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Certificado revocado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Certificado no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeCertificate(
            @Parameter(description = "ID del certificado a revocar", required = true) @PathVariable Long id) {
        log.info("DELETE /certificates/{} - Revocando certificado", id);
        certificateService.revokeCertificate(id);
        return ResponseEntity.noContent().build();
    }
}