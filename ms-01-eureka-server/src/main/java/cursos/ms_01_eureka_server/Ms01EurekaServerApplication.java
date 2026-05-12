package cursos.ms_01_eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // Anotacion para habilitar el servidor Eureka
public class Ms01EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ms01EurekaServerApplication.class, args);
	}

}
