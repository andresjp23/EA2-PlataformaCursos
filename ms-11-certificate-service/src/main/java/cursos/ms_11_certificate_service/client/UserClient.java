package cursos.ms_11_certificate_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cursos.ms_11_certificate_service.dto.remote.UserProfileResponse;

// Cliente Feign para comunicarse con user-service
// Se usa para obtener el nombre del estudiante
@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {

    @GetMapping("/users/{id}")
    UserProfileResponse getUserById(@PathVariable("id") Long id);
}