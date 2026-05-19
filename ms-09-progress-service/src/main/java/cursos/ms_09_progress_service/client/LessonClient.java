package cursos.ms_09_progress_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lesson-service", url = "http://localhost:8084")
public interface LessonClient {

    @GetMapping("/lessons/course/{courseId}")
    Object getLessonsByCourseId(@PathVariable("courseId") Long courseId);
}