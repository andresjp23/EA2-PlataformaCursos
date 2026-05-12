Fase 1: Infraestructura (1-2 días)
#	Nombre	Puerto	Dependencias
ms-01-eureka	Eureka Server	8761	Eureka Server
ms-02-gateway	API Gateway	8080	Gateway, Resilience4J
Fase 2: Autenticación y Usuarios (3-4 días)
#	Nombre	Puerto	Dependencias
ms-03-auth	auth-service	8081	Web, Security, JPA, MySQL, JWT
ms-04-user	user-service	8082	Web, JPA, MySQL
ms-05-security	security-service	8083	Web, JPA, MySQL
Fase 3: Catálogo de Cursos (4-5 días)
#	Nombre	Puerto	Dependencias
ms-06-category	category-service	8086	Web, JPA, MySQL
ms-07-course	course-service	8084	Web, JPA, MySQL, Feign
ms-08-lesson	lesson-service	8085	Web, JPA, MySQL, Feign
Fase 4: Inscripción y Progreso (3-4 días)
#	Nombre	Puerto	Dependencias
ms-09-enrollment	enrollment-service	8087	Web, JPA, MySQL, Feign
ms-10-progress	progress-service	8088	Web, JPA, MySQL, Feign
Fase 5: Evaluación y Certificados (3-4 días)
#	Nombre	Puerto	Dependencias
ms-11-evaluation	evaluation-service	8089	Web, JPA, MySQL, Feign
ms-12-certificate	certificate-service	8090	Web, JPA, MySQL, Feign
---
Cada servicio seguirá esta estructura interna:
src/main/java/com/plataforma/
├── controller/
├── service/
├── repository/
├── model/entity/
├── dto/
├── config/
└── client/ (Feign)