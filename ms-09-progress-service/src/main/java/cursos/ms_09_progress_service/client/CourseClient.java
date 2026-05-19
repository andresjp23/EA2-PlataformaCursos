package cursos.ms_09_progress_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "course-service", url = "http://localhost:8085")
public interface CourseClient {

    @GetMapping("/courses/{id}")
    Object getCourseById(@PathVariable("id") Long id);
}