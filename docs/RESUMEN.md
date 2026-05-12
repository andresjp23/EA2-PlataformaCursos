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

### Servicios de Negocio (Pendientes - 6 servicios)

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| `security-service` | 8084 | Gestión de roles, permisos y autorización |
| `course-service` | 8085 | Catálogo de cursos, reviews y ratings |
| `lesson-service` | 8086 | Contenido de lecciones (texto e imágenes) |
| `enrollment-service` | 8087 | Inscripciones de estudiantes a cursos |
| `progress-service` | 8088 | Seguimiento de avance por curso |
| `evaluation-service` | 8089 | Evaluaciones, exámenes y calificaciones |
| `certificate-service` | 8090 | Generación y validación de certificados |

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

### Servicios Implementados: 5 de 12

- ✅ Eureka Server
- ✅ API Gateway
- ✅ Auth Service (funcional)
- ✅ User Service (CRUD completo)
- ✅ Category Service (CRUD completo)

### Pendiente por Implementar (6 servicios)

1. ~~Security Service~~ (NO se implementa - roles en JWT)
2. Course Service (catálogo de cursos)
3. Lesson Service (contenido educativo)
4. Enrollment Service (inscripciones)
5. Progress Service (seguimiento)
6. Evaluation Service (exámenes)
7. Certificate Service (certificados)

---

> Este proyecto es un sistema completo de microservicios con Spring Boot, siguiendo las mejores prácticas de arquitectura distribuida con service discovery, API gateway, autenticación JWT y bases de datos separadas por servicio.
