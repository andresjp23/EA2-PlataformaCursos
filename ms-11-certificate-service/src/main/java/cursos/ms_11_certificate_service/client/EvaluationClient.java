package cursos.ms_11_certificate_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cursos.ms_11_certificate_service.dto.remote.EvaluationResponse;

// Cliente Feign para comunicarse con evaluation-service
// Se usa para obtener la nota de aprobacion del curso
@FeignClient(name = "evaluation-service", url = "http://localhost:8088")
public interface EvaluationClient {

    @GetMapping("/evaluations/course/{courseId}")
    List<EvaluationResponse> getEvaluationsByCourse(@PathVariable("courseId") Long courseId);
}