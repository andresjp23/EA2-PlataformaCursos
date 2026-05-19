package cursos.ms_09_progress_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Ms09ProgressServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ms09ProgressServiceApplication.class, args);
    }
}