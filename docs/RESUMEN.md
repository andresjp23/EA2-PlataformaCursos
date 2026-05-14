# Plataforma de Cursos Online

Backend completo de una plataforma educativa online (tipo "mini-Coursera") construido con arquitectura de microservicios.

---

## 1. Propósito del Proyecto

Este sistema permite:

- **Registro e identificación** de usuarios con roles diferenciados (Estudiantes, Instructores, Administradores)
- **Gestión de cursos** con catálogo, categorías y contenido multimedia
- **Inscripción** de estudiantes en cursos disponibles
- **Seguimiento de progreso** del aprendizaje (lecciones secuenciales)
- **Sistema de evaluaciones** con preguntas y calificaciones
- **Generación automática de certificados** al aprobar cursos

---

## 2. Microservicios

### Servicios de Infraestructura

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| `ms-01-eureka-server` | 8761 | ✅ Implementado | Service Registry - Registro central de todos los microservicios |
| `ms-02-api-gateway` | 8080 | ✅ Implementado | Entry point único - Enruta peticiones a servicios, circuit breaker |
| `ms-03-auth-service` | 8081 | ✅ Implementado | Autenticación JWT, login, registro, gestión de usuarios |
| `ms-04-user-service` | 8082 | 🔄 Parcialmente | Gestión de perfiles de usuario (CRUD completo) |
| `ms-05-category-service` | 8083 | ✅ Implementado | Gestión de categorías de cursos (CRUD completo) |

### Servicios de Negocio

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| `ms-06-lesson-service` | 8086 | ✅ Implementado | Contenido de lecciones (texto e imágenes) |
| `ms-07-course-service` | 8087 | ✅ Implementado | Catálogo de cursos |
| `ms-08-enrollment-service` | 8088 | ✅ Implementado | Inscripciones (Feign: course + user) |

**Pendientes - 4 servicios**

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `security-service` | 8084 | ~~Gestión de roles~~ (NO se implementa - roles en JWT) |
| `course-service` | 8085 | ~~Catálogo de cursos~~ (implementado en ms-07) |
| `enrollment-service` | 8088 | ~~Inscripciones~~ (implementado con Feign) |
| `progress-service` | 8089 | Seguimiento de avance por curso |
| `evaluation-service` | 8090 | Evaluaciones, exámenes y calificaciones |
| `certificate-service` | 8091 | Generación y validación de certificados |

---

## 3. Stack Tecnológico

### Framework y Lenguaje

- **Java**: JDK 21
- **Spring Boot**:
  - Eureka/Gateway: `4.0.6`
  - Auth-service: `3.5.14`
- **Spring Cloud**: `2025.1.1` (Eureka/Gateway) y `2025.0.2` (Auth)

### Bases de Datos

| Componente | Detalle |
|------------|---------|
| Motor | MySQL 8.0+ |
| Puerto | 3306 (algunos servicios usan 3307) |
| ORM | Hibernate JPA con DDL automático (`update`) |
| Patrón | Database-per-microservice (10 bases de datos separadas) |

### Infraestructura y Comunicación

| Componente | Tecnología |
|------------|------------|
| Service Registry | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Comunicación inter-servicios | Feign Client (planificado) |
| Balanceo de carga | Ribbon + Eureka |
| Circuit Breaker | Resilience4J |
| Autenticación | JWT (JJWT 0.12.6) |
| Encriptación | BCrypt |

### Gestión y Utilidades

- **Build**: Maven (con wrapper en cada módulo)
- **Boilerplate**: Lombok
- **Validación**: Jakarta Bean Validation

---

## 4. Comunicación entre Microservicios

### Arquitectura de Comunicación

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│   Cliente   │────▶│  API Gateway │────▶│   Eureka    │
│             │     │    (8080)    │     │  Server     │
└─────────────┘     └──────────────┘     │  (8761)     │
                                         └─────────────┘
                                               │
                    ┌──────────────────────────┴──────────────────────────┐
                    ▼                          ▼                          ▼
           ┌───────────────┐          ┌───────────────┐          ┌───────────────┐
│ Auth Service │          │ User Service  │          │Category Svc   │
            │   (8081)      │          │   (8082)      │          │   (8083)      │
           └───────────────┘          └───────────────┘          └───────────────┘
```

### Comunicación Síncrona (Feign Client - Planificado)

Los servicios se comunicarán mediante interfaces Feign:

```java
@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
```

### Rutas del API Gateway

```yaml
routes:
  - id: ms-03-auth-service
    uri: http://localhost:8081
    predicates:
      - Path=/auth/**
  - id: ms-04-user-service
    uri: http://localhost:8082
    predicates:
      - Path=/users/**
  - id: ms-05-category-service
    uri: http://localhost:8083
    predicates:
      - Path=/categories/**
- id: ms-07-course-service
          uri: http://localhost:8087
          predicates:
            - Path=/courses/**
  - id: ms-08-enrollment-service
          uri: http://localhost:8088
          predicates:
            - Path=/enrollments/**
```

### Flujo de Descubrimiento

1. Cada servicio se registra en Eureka al iniciar
2. El Gateway consulta Eureka para descubrir servicios
3. Load balancing automático entre instancias

---

## 5. Principales API Endpoints

### Auth Service (Puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Login con username y password |
| POST | `/auth/register` | Registro de nuevo usuario |

**Request Login:**
```json
{ "username": "usuario@example.com", "password": "miPassword123" }
```

**Response:**
```json
{ "token": "eyJhbGciOiJIUzI1NiIs...", "role": "STUDENT" }
```

### User Service (Puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/users` | Crear perfil de usuario |
| GET | `/users/{id}` | Obtener perfil por ID |
| GET | `/users/auth/{authUserId}` | Obtener perfil por ID de auth-service |
| GET | `/users/email/{email}` | Buscar perfil por email |
| PUT | `/users/{id}` | Actualizar perfil |
| DELETE | `/users/{id}` | Eliminar perfil (soft delete) |

### Category Service (Puerto 8083)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/categories` | Crear nueva categoría |
| GET | `/categories` | Listar categorías activas |
| GET | `/categories/all` | Listar todas las categorías (incluye inactivas) |
| GET | `/categories/{id}` | Obtener categoría por ID |
| PUT | `/categories/{id}` | Actualizar categoría |
| DELETE | `/categories/{id}` | Eliminar categoría (soft delete) |
| PATCH | `/categories/{id}/enable` | Reactivar categoría eliminada |

**Request (POST/PUT):**
```json
{
    "name": "Programación",
    "description": "Cursos de desarrollo de software",
    "icon": "💻"
}
```

**Response:**
```json
{
    "id": 1,
    "name": "Programación",
    "description": "Cursos de desarrollo de software",
    "icon": "💻",
    "active": true
}
```

### Lesson Service (Puerto 8086)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/lessons` | Crear nueva lección |
| GET | `/lessons/{id}` | Obtener lección por ID |
| GET | `/lessons/course/{courseId}` | Listar lecciones por curso |
| PUT | `/lessons/{id}` | Actualizar lección |
| DELETE | `/lessons/{id}` | Eliminar lección (soft delete) |

**Request (POST/PUT):**
```json
{
    "courseId": 1,
    "title": "Introducción a Java",
    "content": "Contenido de la lección...",
    "videoUrl": "https://youtube.com/...",
    "orderIndex": 1,
    "durationMinutes": 15
}
```

**Response:**
```json
{
    "id": 1,
    "courseId": 1,
    "title": "Introducción a Java",
    "content": "Contenido de la lección...",
    "videoUrl": "https://youtube.com/...",
    "orderIndex": 1,
    "durationMinutes": 15,
    "active": true
}
```

### Course Service (Puerto 8087)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/courses` | Crear nuevo curso |
| GET | `/courses` | Listar todos los cursos activos |
| GET | `/courses/{id}` | Obtener curso por ID |
| GET | `/courses/category/{categoryId}` | Listar cursos por categoría |
| GET | `/courses/instructor/{instructorId}` | Listar cursos por instructor |
| PUT | `/courses/{id}` | Actualizar curso |
| DELETE | `/courses/{id}` | Eliminar curso (soft delete) |
| PATCH | `/courses/{id}/enable` | Reactivar curso |

**Request (POST/PUT):**
```json
{
    "categoryId": 1,
    "instructorId": 2,
    "title": "Java Fundamentals",
    "description": "Curso completo de Java desde cero",
    "imageUrl": "https://example.com/java.jpg",
    "price": 49.99
}
```

### Enrollment Service (Puerto 8088) - CON FEIGN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/enrollments` | Crear inscripción (valida curso + usuario por Feign) |
| GET | `/enrollments/{id}` | Obtener inscripción por ID |
| GET | `/enrollments/user/{userId}` | Listar inscripciones por usuario |
| GET | `/enrollments/course/{courseId}` | Listar inscripciones por curso |
| GET | `/enrollments` | Listar todas las inscripciones activas |
| PATCH | `/enrollments/{id}/status?status=COMPLETED` | Actualizar estado |
| DELETE | `/enrollments/{id}` | Cancelar inscripción (soft delete) |

**Request (POST):**
```json
{
    "userId": 1,
    "courseId": 1
}
```

**Flujo Feign en POST /enrollments:**
```
1. enrollment-service recibe petición
2. Feign → course-service.getCourseById(courseId) → valida que existe
3. Feign → user-service.getUserById(userId) → valida que existe
4. Si ambos existen → crear inscripción
5. Si alguno falla → throw RuntimeException
```

---

## 6. Estructura de Base de Datos

### db_auth (Auth Service)

**Tabla: `users`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(100) | UNIQUE, NOT NULL |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL (BCrypt) |
| enabled | BOOLEAN | DEFAULT TRUE |
| role_id | BIGINT | FOREIGN KEY |

**Tabla: `roles`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(50) | UNIQUE, NOT NULL |

> Datos iniciales: `ADMIN`, `TEACHER`, `STUDENT`

### db_users (User Service)

**Tabla: `user_profiles`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| authUserId | BIGINT | UNIQUE, NOT NULL (FK a auth-service) |
| firstName | VARCHAR(100) | NOT NULL |
| lastName | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| profilePicture | VARCHAR(500) | Opcional |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_categories (Category Service)

**Tabla: `categories`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | UNIQUE, NOT NULL |
| description | TEXT | NOT NULL |
| icon | VARCHAR(100) | Opcional |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_lessons (Lesson Service)

**Tabla: `lessons`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| courseId | BIGINT | NOT NULL (FK a course-service) |
| title | VARCHAR(255) | UNIQUE, NOT NULL |
| content | TEXT | NOT NULL |
| videoUrl | VARCHAR(500) | Opcional |
| orderIndex | INT | Opcional |
| durationMinutes | INT | Opcional |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_courses (Course Service)

**Tabla: `courses`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| categoryId | BIGINT | NOT NULL |
| instructorId | BIGINT | NOT NULL |
| title | VARCHAR(255) | UNIQUE, NOT NULL |
| description | TEXT | NOT NULL |
| imageUrl | VARCHAR(500) | Opcional |
| price | DOUBLE | Opcional |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_enrollments (Enrollment Service)

**Tabla: `enrollments`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| userId | BIGINT | NOT NULL (FK a user-service) |
| courseId | BIGINT | NOT NULL (FK a course-service) |
| enrolledAt | DATETIME | DEFAULT CURRENT_TIMESTAMP |
| status | VARCHAR(50) | DEFAULT 'ACTIVE' |
| isActive | BOOLEAN | DEFAULT TRUE |

---

## 7. Modelos y Entidades Principales

### User.java (Auth Service)

```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
}
```

### Role.java (Auth Service)

```java
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // ADMIN, TEACHER, STUDENT
}
```

### UserProfile.java (User Service)

```java
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long authUserId; // Referencia al usuario en auth-service

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String profilePicture;

    @Column
    private boolean isActive = true;
}
```

### Category.java (Category Service)

```java
@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column
    private String icon;

    @Column
    private boolean isActive = true;
}
```

### Lesson.java (Lesson Service)

```java
@Entity
@Table(name = "lessons")
public class Lesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long courseId;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String videoUrl;

    @Column
    private Integer orderIndex;

    @Column
    private Integer durationMinutes;

    @Column
    private boolean isActive = true;
}
```

### Course.java (Course Service)

```java
@Entity
@Table(name = "courses")
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Long instructorId;

    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column
    private String imageUrl;

    @Column
    private Double price;

    @Column
    private boolean isActive = true;
}
```

### Enrollment.java (Enrollment Service)

```java
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long courseId;

    @Column
    private LocalDateTime enrolledAt;

    @Column
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @Column
    private boolean isActive = true;
}
```

---

## 8. Seguridad Implementada

### JWT (JSON Web Tokens)

| Propiedad | Valor |
|-----------|-------|
| Algoritmo | HS256 (HMAC-SHA256) |
| Secret | Configurado en `application.properties` |
| Expiración | 86400000ms (24 horas) - configurable |

### Flujo de Autenticación

```
1. Cliente ──▶ POST /auth/login {username, password}
2. AuthController ──▶ AuthenticationManager.authenticate()
3. UserDetailsServiceImpl ──▶ Busca usuario en BD
4. Valida contraseña con BCrypt
5. JwtService.generateToken(username, role) ──▶ Retorna token JWT con rol
6. Cliente recibe {token: "eyJhbGciOiJIUzI1NiIs...", "role": "STUDENT"}
7. Requests posteriores incluyen: Authorization: Bearer <token>
```

### JWT Claims

El token JWT incluye el rol del usuario para authorization sin consultar auth-service:

```json
{
  "sub": "usuario123",
  "role": "STUDENT",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Autorización con Roles

Los roles se incluyen en el JWT token, permitiendo:
- Validación de permisos sin consultar auth-service
- @PreAuthorize("hasRole('ADMIN')") en endpoints protegidos
- Ownership validation en servicios

| Rol | Acceso |
|-----|--------|
| ADMIN | Endpoints de gestión (categorías, usuarios) |
| TEACHER | Crear/editar cursos y lecciones propias |
| STUDENT | Ver cursos, inscribirse, ver progreso |

### Rutas Públicas vs Protegidas

```java
// SecurityConfig.java
.requestMatchers("/auth/login", "/auth/register").permitAll()
.anyRequest().authenticated()
```

### Filtro JWT en Gateway

```java
// JwtAuthenticationFilter.java
// Rutas públicas: /auth/login, /auth/register
// Valida token en header Authorization: Bearer <token>
// 401 si token inválido o expirado
```

---

## 9. Estructura del Proyecto

```
EA2-PlataformaCursos/
├── docs/
│   ├── PROYECTO_PLATAFORMA_CURSOS.md
│   ├── PLAN_DESARROLLO.md
│   ├── ENDPOINTS_DOCUMENTACION.md
│   ├── DIAGRAMA_ARQUITECTURA.txt
│   ├── ESTRUCTURA_PROYECTO.txt
│   └── RESUMEN_EJECUTIVO.txt
│
├── ms-01-eureka-server/
│   ├── src/main/java/cursos/ms_01_eureka_server/
│   │   └── Ms01EurekaServerApplication.java
│   └── src/main/resources/application.properties
│
├── ms-02-api-gateway/
│   ├── src/main/java/cursos/ms_02_api_gateway/
│   │   ├── Ms02ApiGatewayApplication.java
│   │   ├── config/GatewayConfig.java
│   │   └── filter/JwtAuthenticationFilter.java
│   └── src/main/resources/application.yaml
│
├── ms-03-auth-service/
│   ├── src/main/java/cursos/ms_03_auth_service/
│   │   ├── Ms03AuthServiceApplication.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   └── DataInitializer.java
│   │   ├── controller/AuthController.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── model/entity/
│   │   │   ├── User.java
│   │   │   └── Role.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── RoleRepository.java
│   │   ├── service/AuthService.java
│   │   └── security/
│   │       ├── filter/JwtAuthFilter.java
│   │       └── jwt/JwtService.java
│   └── src/main/resources/application.properties
│
├── ms-04-user-service/
│   ├── src/main/java/cursos/ms_04_user_service/
│   │   ├── Ms04UserServiceApplication.java
│   │   ├── controller/UserProfileController.java
│   │   ├── service/UserProfileService.java
│   │   ├── repository/UserProfileRepository.java
│   │   ├── model/entity/UserProfile.java
│   │   ├── dto/
│   │   │   ├── UserProfileRequest.java
│   │   │   └── UserProfileResponse.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── ErrorResponse.java
│   └── src/main/resources/application.properties
│
    └── ms-05-category-service/
    ├── src/main/java/cursos/ms_05_category_service/
    │   ├── Ms05CategoryServiceApplication.java
    │   ├── model/entity/Category.java
    │   ├── repository/CategoryRepository.java
    │   ├── dto/
    │   │   ├── CategoryRequest.java
    │   │   └── CategoryResponse.java
    │   ├── service/
    │   │   └── CategoryService.java
    │   ├── controller/
    │   │   └── CategoryController.java
    │   └── exception/
    │       ├── ErrorResponse.java
    │       └── GlobalExceptionHandler.java
    └── src/main/resources/application.properties
│
└── ms-06-lesson-service/
    ├── src/main/java/cursos/ms_06_lesson_service/
    │   ├── Ms06LessonServiceApplication.java
    │   ├── model/entity/Lesson.java
    │   ├── repository/LessonRepository.java
    │   ├── dto/
    │   │   ├── LessonRequest.java
    │   │   └── LessonResponse.java
    │   ├── service/
    │   │   └── LessonService.java
    │   ├── controller/
    │   │   └── LessonController.java
│       └── exception/
│           ├── ErrorResponse.java
│           └── GlobalExceptionHandler.java
    └── src/main/resources/application.properties
│
└── ms-07-course-service/
    ├── src/main/java/cursos/ms_07_course_service/
    │   ├── Ms07CourseServiceApplication.java
    │   ├── model/entity/Course.java
    │   ├── repository/CourseRepository.java
    │   ├── dto/
    │   │   ├── CourseRequest.java
    │   │   └── CourseResponse.java
    │   ├── service/
    │   │   └── CourseService.java
    │   ├── controller/
    │   │   └── CourseController.java
    │   └── exception/
    │       ├── ErrorResponse.java
    │       └── GlobalExceptionHandler.java
    └── src/main/resources/application.properties
│
└── ms-08-enrollment-service/
    ├── src/main/java/cursos/ms_08_enrollment_service/
    │   ├── Ms08EnrollmentServiceApplication.java
    │   ├── model/entity/Enrollment.java
    │   ├── repository/EnrollmentRepository.java
    │   ├── dto/
    │   │   ├── EnrollmentRequest.java
    │   │   └── EnrollmentResponse.java
    │   ├── service/
    │   │   └── EnrollmentService.java
    │   ├── controller/
    │   │   └── EnrollmentController.java
    │   ├── client/
    │   │   ├── CourseClient.java       ← Feign
    │   │   └── UserClient.java          ← Feign
    │   └── exception/
    │       ├── ErrorResponse.java
    │       └── GlobalExceptionHandler.java
    └── src/main/resources/application.properties
```

---

## 10. Roles del Sistema

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **ADMIN** | Gestor de la plataforma | Gestión completa de usuarios, categorías, reportes |
| **TEACHER** | Creador de contenido | Crear/editar cursos, lecciones, evaluaciones propias |
| **STUDENT** | Consumidor de contenido | Inscribirse, estudiar, rendir exámenes, descargar certificados |

---

## 11. Configuraciones Clave

### Eureka Server (`application.properties`)

```properties
spring.application.name=ms-01-eureka-server
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### API Gateway (`application.yaml`)

```yaml
server:
  port: 8080
spring:
  cloud:
    gateway:
      routes:
        - id: ms-03-auth-service
          uri: http://localhost:8081
          predicates:
            - Path=/auth/**
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
jwt:
  secret: ZXN0YS1lcy1taS1jbGF2ZS1zZWNyZXRh...
```

### Auth Service (`application.properties`)

```properties
spring.application.name=ms-03-auth-service
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/db_auth
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
jwt.secret=ZXN0YS1lcy1taS1jbGF2ZS1zZWNyZXRh...
jwt.expiration=86400000
```

---

## 12. Estado Actual

### Servicios Implementados: 8 de 12

- ✅ Eureka Server
- ✅ API Gateway
- ✅ Auth Service (funcional)
- ✅ User Service (CRUD completo)
- ✅ Category Service (CRUD completo)
- ✅ Lesson Service (CRUD completo)
- ✅ Course Service (CRUD completo)
- ✅ Enrollment Service (CRUD + Feign)

### Pendiente por Implementar (4 servicios)

1. ~~Security Service~~ (NO se implementa - roles en JWT)
2. ~~Course Service~~ (implementado en ms-07)
3. ~~Enrollment Service~~ (implementado con Feign en ms-08)
4. Progress Service (seguimiento)
5. Evaluation Service (exámenes)
6. Certificate Service (certificados)

---

## 13. Feign Client - Comunicación entre Microservicios

### Qué es Feign Client

Feign es un **cliente HTTP declarativo** que permite que un microservicio se comunique con otro mediante interfaces Java, en lugar de escribir código HTTP bajo nivel.

```
 enrollment-service              lesson-service
 ┌─────────────────┐           ┌─────────────────┐
 │                 │ ──GET/──▶ │                 │
 │ "¿Cuántas       │           │ "Aquí están     │
 │  lecciones      │ ◀─datos──│  las lecciones" │
 │  tiene el 1?"   │           │                 │
 └─────────────────┘           └─────────────────┘
```

### Cómo funciona (en concepto)

1. Un servicio define una **interfaz** con `@FeignClient` que sabe cómo llamar a otro servicio
2. El código llama métodos de esa interfaz como si fueran métodos locales
3. Feign traduce esas llamadas a requests HTTP reales al otro servicio

### Servicios que se comunican entre sí

| Quien llama | Llama a | Para qué |
|-------------|---------|----------|
| `enrollment-service` | `course-service` | Validar que el curso existe antes de inscribir |
| `enrollment-service` | `user-service` | Validar que el estudiante existe |
| `progress-service` | `lesson-service` | Saber cuántas lecciones tiene el curso |
| `progress-service` | `course-service` | Vincular progreso con datos del curso |
| `certificate-service` | `evaluation-service` | Verificar que el estudiante aprobó |
| `certificate-service` | `course-service` | Obtener datos del curso para el certificado |

### Ejemplo de interfaz Feign

```java
@FeignClient(name = "course-service", url = "http://localhost:8087")
public interface CourseClient {
    @GetMapping("/courses/{id}")
    CourseResponse getCourseById(@PathVariable("id") Long id);
}
```

### Estructura para implementar Feign

```
ms-XX-service/
├── client/                          # NUEVO: interfaces Feign
│   ├── CourseClient.java
│   └── UserClient.java
├── service/
│   └── AlgoService.java             # Usa los clients
```

### Dependencias necesarias

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Habilitar Feign en la aplicación

```java
@SpringBootApplication
@EnableFeignClients  // <-- Anotación necesaria
public class Ms0XServiceApplication { ... }
```

### Manejo de errores en llamadas Feign

```java
@Service
public class EnrollmentService {
    public void enrollStudent(Long userId, Long courseId) {
        try {
            CourseResponse course = courseClient.getCourseById(courseId);
            // continuar con la inscripción
        } catch (FeignException e) {
            log.error("Error al consultar course-service: {}", e.getMessage());
            throw new RuntimeException("No se pudo verificar el curso");
        }
    }
}
```

---

## 14. Plan de Acción - Implementación de Servicios y Feign

### Fase 1: Servicios base (Completados)

| Servicio | Estado | Puerto |
|----------|--------|--------|
| ms-01-eureka-server | ✅ | 8761 |
| ms-02-api-gateway | ✅ | 8080 |
| ms-03-auth-service | ✅ | 8081 |
| ms-04-user-service | ✅ | 8082 |
| ms-05-category-service | ✅ | 8083 |
| ms-06-lesson-service | ✅ | 8086 |
| ms-07-course-service | ✅ | 8087 |

### Fase 2: Próximos servicios con Feign

| Orden | Servicio | Puerto | Feign a implementar |
|-------|----------|--------|---------------------|
| 1 | `progress-service` | 8089 | Consulta `lesson-service` + `course-service` |
| 2 | `evaluation-service` | 8090 | Consulta `user-service` + `course-service` |
| 3 | `certificate-service` | 8091 | Consulta `evaluation-service` + `course-service` |

### Fase 3: Servicios completados con Feign

| Servicio | Puerto | Feign implementado |
|----------|--------|-------------------|
| `enrollment-service` | 8088 | ✅ Consulta `course-service` + `user-service` |

### Flujos de negocio a implementar

**Inscripción (enrollment):**
```
1. Estudiante solicita inscripción en curso X
2. enrollment-service valida que curso X existe (Feign → course-service)
3. enrollment-service valida que estudiante Y existe (Feign → user-service)
4. Se crea inscripción en BD local
5. Se retorna confirmación
```

**Seguimiento de progreso (progress):**
```
1. Estudiante inicia curso X
2. progress-service consulta lecciones del curso (Feign → lesson-service)
3. progress-service obtiene datos del curso (Feign → course-service)
4. Se crea registro de progreso en BD local
5. Cada lección completada actualiza el progreso
```

**Generación de certificado (certificate):**
```
1. Estudiante solicita certificado del curso X
2. certificate-service verifica aprobación (Feign → evaluation-service)
3. certificate-service obtiene datos del curso (Feign → course-service)
4. Se genera certificado en BD local
5. Se retorna certificado
```

### Checklist para cumplir rúbrica de evaluación

- [ ] 10 microservicios implementados (contando los de infraestructura)
- [ ] Comunicación Feign entre servicios de negocio
- [ ] Manejo de errores en llamadas inter-servicios
- [ ] Logs en cada llamada Feign
- [ ] Base de datos propia por servicio

---

> Este proyecto es un sistema completo de microservicios con Spring Boot, siguiendo las mejores prácticas de arquitectura distribuida con service discovery, API gateway, autenticación JWT y bases de datos separadas por servicio.
