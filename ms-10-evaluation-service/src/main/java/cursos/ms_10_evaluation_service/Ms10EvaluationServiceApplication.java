package cursos.ms_10_evaluation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Ms10EvaluationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ms10EvaluationServiceApplication.class, args);
    }
}