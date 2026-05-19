package cursos.ms_11_certificate_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cursos.ms_11_certificate_service.dto.remote.ProgressResponse;

// Cliente Feign para comunicarse con progress-service
// Se usa para verificar que el estudiante completo el 100% del curso
@FeignClient(name = "progress-service", url = "http://localhost:8087")
public interface ProgressClient {

    @GetMapping("/progress/user/{userId}/course/{courseId}")
    ProgressResponse getProgressByUserAndCourse(
            @PathVariable("userId") Long userId,
            @PathVariable("courseId") Long courseId);
}