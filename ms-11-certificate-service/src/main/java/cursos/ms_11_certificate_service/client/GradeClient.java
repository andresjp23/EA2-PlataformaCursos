package cursos.ms_11_certificate_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cursos.ms_11_certificate_service.dto.remote.GradeResponse;

// Cliente Feign para comunicarse con grade-service
// Se usa para obtener la nota del estudiante en una evaluacion
@FeignClient(name = "grade-service", url = "http://localhost:8090")
public interface GradeClient {

    @GetMapping("/grades/student/{studentId}/evaluation/{evaluationId}")
    GradeResponse getGradeByStudentAndEvaluation(
            @PathVariable("studentId") Long studentId,
            @PathVariable("evaluationId") Long evaluationId);
}