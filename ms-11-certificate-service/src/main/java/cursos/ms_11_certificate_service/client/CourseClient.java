package cursos.ms_11_certificate_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cursos.ms_11_certificate_service.dto.remote.CourseResponse;

// Cliente Feign para comunicarse con course-service
// Se usa para obtener el titulo del curso
@FeignClient(name = "course-service", url = "http://localhost:8085")
public interface CourseClient {

    @GetMapping("/courses/{id}")
    CourseResponse getCourseById(@PathVariable("id") Long id);
}