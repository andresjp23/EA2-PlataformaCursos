package cursos.ms_11_certificate_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Ms11CertificateServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Ms11CertificateServiceApplication.class, args);
    }
}

