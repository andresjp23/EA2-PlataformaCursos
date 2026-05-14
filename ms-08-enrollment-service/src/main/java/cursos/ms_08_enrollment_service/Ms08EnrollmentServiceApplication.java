package cursos.ms_08_enrollment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Ms08EnrollmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ms08EnrollmentServiceApplication.class, args);
    }
}