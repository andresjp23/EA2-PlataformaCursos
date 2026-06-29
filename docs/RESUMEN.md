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
- **Registro de notas** de estudiantes en evaluaciones
- **Generación automática de certificados** al aprobar cursos (progreso 100% + evaluación aprobada)

---

## 2. Microservicios

### Servicios de Infraestructura

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| `ms-01-eureka-server` | 8761 | ✅ Implementado | Service Registry - Registro central de todos los microservicios |
| `ms-02-api-gateway` | 8080 | ✅ Implementado | Entry point único - Enruta peticiones a servicios, circuit breaker |

### Servicios de Negocio

| Servicio | Puerto | Estado | Descripción |
|----------|--------|--------|-------------|
| `ms-03-auth-service` | 8081 | ✅ Implementado | Autenticación JWT, login, registro, gestión de usuarios |
| `ms-04-user-service` | 8082 | ✅ Implementado | Gestión de perfiles de usuario (CRUD completo) |
| `ms-05-category-service` | 8083 | ✅ Implementado | Gestión de categorías de cursos (CRUD completo) |
| `ms-06-lesson-service` | 8084 | ✅ Implementado | Contenido de lecciones (texto e imágenes) |
| `ms-07-course-service` | 8085 | ✅ Implementado | Catálogo de cursos |
| `ms-08-enrollment-service` | 8086 | ✅ Implementado | Inscripciones (Feign: course + user) |
| `ms-09-progress-service` | 8087 | ✅ Implementado | Seguimiento de progreso (Feign: lesson + course) |
| `ms-10-evaluation-service` | 8088 | ✅ Implementado | Evaluaciones (Feign: course) |
| `ms-11-certificate-service` | 8089 | ✅ Implementado | Certificados (Feign: user + course + progress + evaluation + grade) |
| `ms-12-grade-service` | 8090 | ✅ Implementado | Registro de notas de estudiantes |

**Total: 10 servicios de negocio + 2 de infraestructura = 12 microservicios**

---

## 3. Stack Tecnológico

### Framework y Lenguaje

- **Java**: JDK 21
- **Spring Boot**: 3.5.14/3.5.15-SNAPSHOT
- **Spring Cloud**: `2025.0.2`

### Bases de Datos

| Componente | Detalle |
|------------|---------|
| Motor | MySQL 8.0+ |
| Puerto | 3306 |
| ORM | Hibernate JPA con DDL automático (`update`) |
| Patrón | Database-per-microservice (12 bases de datos separadas) |

### Infraestructura y Comunicación

| Componente | Tecnología |
|------------|------------|
| Service Registry | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Comunicación inter-servicios | Feign Client |
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
    ┌──────────────────────────────────────────┼──────────────────────────────────────────┐
    ▼                                          ▼                                          ▼
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│  Auth   │    │  User   │    │Category │    │ Lesson  │    │ Course  │    │Enrollment│
│ (8081)  │    │ (8082)  │    │ (8083)  │    │ (8084)  │    │ (8085)  │    │  (8086) │
└─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘    └─────────┘
                                                                    │
    ┌────────────────────────────────────────────────────────────────┘
    ▼                                          ▼                                          ▼
┌─────────┐    ┌─────────┐    ┌──────────────────┐    ┌──────────────────┐
│Progress │    │Evaluation│    │    Certificate   │    │      Grade       │
│ (8087)  │    │ (8088)  │    │     (8089)       │    │     (8090)       │
└─────────┘    └─────────┘    └──────────────────┘    └──────────────────┘
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
  - id: ms-06-lesson-service
    uri: http://localhost:8084
    predicates:
      - Path=/lessons/**
  - id: ms-07-course-service
    uri: http://localhost:8085
    predicates:
      - Path=/courses/**
  - id: ms-08-enrollment-service
    uri: http://localhost:8086
    predicates:
      - Path=/enrollments/**
  - id: ms-09-progress-service
    uri: http://localhost:8087
    predicates:
      - Path=/progress/**
  - id: ms-10-evaluation-service
    uri: http://localhost:8088
    predicates:
      - Path=/evaluations/**
  - id: ms-11-certificate-service
    uri: http://localhost:8089
    predicates:
      - Path=/certificates/**
  - id: ms-12-grade-service
    uri: http://localhost:8090
    predicates:
      - Path=/grades/**
```

---

## 5. Principales API Endpoints

### Auth Service (Puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Login con username y password |
| POST | `/auth/register` | Registro de nuevo usuario |

### User Service (Puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/users` | Crear perfil de usuario |
| GET | `/users/{id}` | Obtener perfil por ID |
| GET | `/users/auth/{authUserId}` | Obtener perfil por ID de auth-service |
| PUT | `/users/{id}` | Actualizar perfil |
| DELETE | `/users/{id}` | Eliminar perfil (soft delete) |

### Category Service (Puerto 8083)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/categories` | Crear nueva categoría |
| GET | `/categories` | Listar categorías activas |
| GET | `/categories/all` | Listar todas las categorías |
| GET | `/categories/{id}` | Obtener categoría por ID |
| PUT | `/categories/{id}` | Actualizar categoría |
| DELETE | `/categories/{id}` | Eliminar categoría (soft delete) |
| PATCH | `/categories/{id}/enable` | Reactivar categoría |

### Lesson Service (Puerto 8084)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/lessons` | Crear nueva lección |
| GET | `/lessons/{id}` | Obtener lección por ID |
| GET | `/lessons/course/{courseId}` | Listar lecciones por curso |
| PUT | `/lessons/{id}` | Actualizar lección |
| DELETE | `/lessons/{id}` | Eliminar lección (soft delete) |

### Course Service (Puerto 8085)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/courses` | Crear nuevo curso |
| GET | `/courses` | Listar todos los cursos activos |
| GET | `/courses/{id}` | Obtener curso por ID |
| GET | `/courses/category/{categoryId}` | Listar cursos por categoría |
| GET | `/courses/instructor/{instructorId}` | Listar cursos por instructor |
| PUT | `/courses/{id}` | Actualizar curso |
| DELETE | `/courses/{id}` | Eliminar curso (soft delete) |

### Enrollment Service (Puerto 8086) - CON FEIGN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/enrollments` | Crear inscripción (valida curso + usuario por Feign) |
| GET | `/enrollments/{id}` | Obtener inscripción por ID |
| GET | `/enrollments/user/{userId}` | Listar inscripciones por usuario |
| GET | `/enrollments/course/{courseId}` | Listar inscripciones por curso |
| GET | `/enrollments` | Listar todas las inscripciones activas |
| PATCH | `/enrollments/{id}/status?status=X` | Actualizar estado |
| DELETE | `/enrollments/{id}` | Cancelar inscripción (soft delete) |

**Feign:** Valida curso (`course-service`) y usuario (`user-service`) antes de crear inscripción.

### Progress Service (Puerto 8087) - CON FEIGN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/progress` | Crear registro de progreso |
| GET | `/progress/{id}` | Obtener progreso por ID |
| GET | `/progress/user/{userId}/course/{courseId}` | Progreso específico |
| GET | `/progress/user/{userId}` | Listar progresos de un usuario |
| PATCH | `/progress/{id}/complete?lessonId=X` | Marcar lección completada |
| PATCH | `/progress/{id}/status?status=X` | Actualizar estado |
| DELETE | `/progress/{id}` | Eliminar progreso (soft delete) |

### Evaluation Service (Puerto 8088) - CON FEIGN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/evaluations` | Crear nueva evaluación |
| GET | `/evaluations/{id}` | Obtener evaluación por ID |
| GET | `/evaluations/course/{courseId}` | Listar evaluaciones por curso |
| GET | `/evaluations` | Listar todas las evaluaciones activas |
| PUT | `/evaluations/{id}` | Actualizar evaluación |
| DELETE | `/evaluations/{id}` | Eliminar evaluación (soft delete) |

**Feign:** Valida curso (`course-service`) antes de crear evaluación.

### Certificate Service (Puerto 8089) - CON FEIGN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/certificates` | Generar certificado (valida progreso + evaluación + nota) |
| GET | `/certificates/{id}` | Obtener certificado por ID |
| GET | `/certificates/code/{code}` | Obtener certificado por código único |
| GET | `/certificates/user/{userId}` | Listar certificados de un usuario |
| DELETE | `/certificates/{id}` | Revocar certificado (soft delete) |

**Feign (5 servicios):**
- `user-service` → Obtener nombre del estudiante
- `course-service` → Obtener título del curso
- `progress-service` → Verificar progreso 100% y estado COMPLETED
- `evaluation-service` → Obtener evaluationId y passingScore
- `grade-service` → Obtener nota del estudiante

**Flujo completo:**
```
1. certificate-service recibe petición {userId, courseId}
2. Feign → user-service.getUserById() → obtiene nombre estudiante
3. Feign → course-service.getCourseById() → obtiene título curso
4. Feign → progress-service.getProgress() → verifica 100% + COMPLETED
5. Feign → evaluation-service.getEvaluationByCourse() → obtiene evaluationId
6. Feign → grade-service.getGrade() → obtiene nota del estudiante
7. Valida: nota >= passingScore
8. Genera código único CERT-XXXXXXXX
9. Guarda certificado y retorna
```

**Request (POST):**
```json
{
    "userId": 1,
    "courseId": 1
}
```

**Response:**
```json
{
    "id": 1,
    "userId": 1,
    "studentName": "Juan Perez",
    "courseId": 1,
    "courseTitle": "Java Fundamentals",
    "finalGrade": 85.0,
    "issuedAt": "2026-05-18T10:30:00",
    "certificateCode": "CERT-A1B2C3D4",
    "active": true
}
```

### Grade Service (Puerto 8090)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/grades` | Registrar nota de un estudiante |
| GET | `/grades/{id}` | Obtener nota por ID |
| GET | `/grades/student/{studentId}/evaluation/{evaluationId}` | Obtener nota específica |
| GET | `/grades/student/{studentId}` | Listar todas las notas de un estudiante |

**Request (POST):**
```json
{
    "studentId": 1,
    "evaluationId": 1,
    "score": 85
}
```

**Response:**
```json
{
    "id": 1,
    "studentId": 1,
    "evaluationId": 1,
    "score": 85,
    "takenAt": "2026-05-18T10:30:00",
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

### db_progress (Progress Service)

**Tabla: `progress`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| userId | BIGINT | NOT NULL |
| courseId | BIGINT | NOT NULL |
| currentLessonId | BIGINT | NOT NULL |
| completedLessons | INT | NOT NULL, DEFAULT 0 |
| totalLessons | INT | NOT NULL, DEFAULT 0 |
| progressPercentage | INT | NOT NULL, DEFAULT 0 |
| status | VARCHAR(50) | DEFAULT 'ACTIVE' |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_evaluations (Evaluation Service)

**Tabla: `evaluations`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| courseId | BIGINT | NOT NULL |
| title | VARCHAR(255) | UNIQUE, NOT NULL |
| description | TEXT | NOT NULL |
| maxScore | INT | NOT NULL, DEFAULT 100 |
| passingScore | INT | NOT NULL, DEFAULT 70 |
| status | VARCHAR(50) | DEFAULT 'PUBLISHED' |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_certificates (Certificate Service)

**Tabla: `certificates`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| userId | BIGINT | NOT NULL |
| studentName | VARCHAR(200) | NOT NULL |
| courseId | BIGINT | NOT NULL |
| courseTitle | VARCHAR(255) | NOT NULL |
| finalGrade | DOUBLE | NOT NULL |
| issuedAt | DATETIME | NOT NULL |
| certificateCode | VARCHAR(50) | UNIQUE, NOT NULL |
| isActive | BOOLEAN | DEFAULT TRUE |

### db_grades (Grade Service)

**Tabla: `grades`**

| Columna | Tipo | Constraints |
|---------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| studentId | BIGINT | NOT NULL |
| evaluationId | BIGINT | NOT NULL |
| score | INT | NOT NULL (0-100) |
| takenAt | DATETIME | NOT NULL |
| isActive | BOOLEAN | DEFAULT TRUE |

---

## 7. Feign Client - Comunicación entre Microservicios

### Servicios que se comunican entre sí

| Quien llama | Llama a | Para qué |
|-------------|---------|----------|
| `enrollment-service` | `course-service` | Validar que el curso existe |
| `enrollment-service` | `user-service` | Validar que el estudiante existe |
| `progress-service` | `lesson-service` | Obtener cantidad de lecciones del curso |
| `progress-service` | `course-service` | Validar que el curso existe |
| `evaluation-service` | `course-service` | Validar que el curso existe |
| `certificate-service` | `user-service` | Obtener nombre del estudiante |
| `certificate-service` | `course-service` | Obtener título del curso |
| `certificate-service` | `progress-service` | Verificar progreso 100% y COMPLETED |
| `certificate-service` | `evaluation-service` | Obtener evaluationId y passingScore |
| `certificate-service` | `grade-service` | Obtener nota del estudiante |

### Flujo de negocio: Certificado

```
1. Estudiante solicita certificado del curso X
2. certificate-service valida que NO existe certificado activo para ese usuario/curso
3. certificate-service obtiene nombre del estudiante (user-service)
4. certificate-service obtiene título del curso (course-service)
5. certificate-service verifica progreso 100% y estado COMPLETED (progress-service)
6. certificate-service obtiene evaluationId y passingScore (evaluation-service)
7. certificate-service obtiene nota del estudiante (grade-service)
8. certificate-service valida: nota >= passingScore
9. Si todo válido → genera certificado con código único CERT-XXXXXXXX
10. Retorna certificado con todos los datos
```

---

## 8. Estado Actual

### Servicios Implementados: 12 de 12

- ✅ Eureka Server (8761)
- ✅ API Gateway (8080)
- ✅ Auth Service (8081)
- ✅ User Service (8082)
- ✅ Category Service (8083)
- ✅ Lesson Service (8084)
- ✅ Course Service (8085)
- ✅ Enrollment Service (8086) - con Feign
- ✅ Progress Service (8087) - con Feign
- ✅ Evaluation Service (8088) - con Feign
- ✅ Certificate Service (8089) - con Feign (5 servicios)
- ✅ Grade Service (8090)

### Checklist para cumplir rúbrica de evaluación

- [x] 10+ microservicios implementados (12 totales)
- [x] Comunicación Feign entre servicios de negocio
- [x] Manejo de errores en llamadas inter-servicios (try/catch)
- [x] Logs en cada llamada Feign (log.info, log.error)
- [x] Base de datos propia por servicio (12 bases de datos)

---

## 11. Bugs Corregidos Durante las Pruebas

Durante el proceso de pruebas con Postman, se descubrieron y corrigieron los siguientes bugs:

| # | Bug | Servicio | Causa | Solución |
|---|-----|----------|-------|----------|
| 1 | `GET /lessons/course/{id}` retornaba error "Query did not return a unique result" | lesson-service | `findByCourseId` usaba `Optional<Lesson>` pero un curso tiene múltiples lecciones | Cambiado a `List<Lesson> findByCourseId(Long courseId)` |
| 2 | `POST /progress` hardcodeaba `totalLessons = 0` | progress-service | El `LessonClient` estaba inyectado pero nunca se usaba | Agregado Feign call a lesson-service para obtener la cantidad real de lecciones |
| 3 | `existsByUserIdAndCourseId` no filtraba por `isActive` | progress-service | Método original buscaba cualquier progreso sin importar estado | Agregado nuevo método `existsByUserIdAndCourseIdAndIsActiveTrue` |
| 4 | `findByUserIdAndCourseId` no filtraba por `isActive` | progress-service | Retornaba progreso inactivo causando errores de unicidad | Agregado nuevo método `findByUserIdAndCourseIdAndIsActiveTrue` |
| 5 | `EvaluationClient` retornaba objeto único pero endpoint retorna lista | certificate-service | El endpoint `/evaluations/course/{courseId}` retorna `List<EvaluationResponse>` | Cambiado tipo de retorno a `List<EvaluationResponse>` |

---

## 12. Roles del Sistema

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **ADMIN** | Gestor de la plataforma | Gestión completa de usuarios, categorías, reportes |
| **TEACHER** | Creador de contenido | Crear/editar cursos, lecciones, evaluaciones propias |
| **STUDENT** | Consumidor de contenido | Inscribirse, estudiar, rendir exámenes, generar certificados |

---

## 13. Estructura del Proyecto

```
EA2-PlataformaCursos/
├── docs/
│   └── resumen.md                    ← Este documento
│
├── ms-01-eureka-server/             # Infraestructura
├── ms-02-api-gateway/               # Infraestructura
│
├── ms-03-auth-service/              # Auth (8081)
├── ms-04-user-service/              # Perfiles usuario (8082)
├── ms-05-category-service/          # Categorías (8083)
├── ms-06-lesson-service/            # Lecciones (8084)
├── ms-07-course-service/            # Cursos (8085)
├── ms-08-enrollment-service/        # Inscripciones (8086) + Feign
├── ms-09-progress-service/          # Progreso (8087) + Feign
├── ms-10-evaluation-service/        # Evaluaciones (8088) + Feign
├── ms-11-certificate-service/       # Certificados (8089) + Feign (5 servicios)
└── ms-12-grade-service/             # Notas estudiantes (8090)
```

---

---

## 14. Despliegue con Docker

El proyecto está preparado para ejecutarse con **Docker Compose**, levantando los 12 microservicios + MySQL en contenedores aislados.

### Requisitos

- Docker Desktop (Windows/Mac) o Docker Engine (Linux)
- No requiere instalar Java, Maven ni MySQL localmente

### Estructura de entrega

```
entrega-cliente/
├── apps/                          # Archivos .jar compilados
│   ├── ms-01-eureka-server.jar
│   ├── ms-02-api-gateway.jar
│   ├── ms-03-auth-service.jar
│   ├── ms-04-user-service.jar
│   ├── ms-05-category-service.jar
│   ├── ms-06-lesson-service.jar
│   ├── ms-07-course-service.jar
│   ├── ms-08-enrollment-service.jar
│   ├── ms-09-progress-service.jar
│   ├── ms-10-evaluation-service.jar
│   ├── ms-11-certificate-service.jar
│   └── ms-12-grade-service.jar
├── docs/
│   └── init.sql                   # Inicialización automática de bases de datos
├── docker-compose.yml             # Orquestación de contenedores
├── arrancar-sistema.bat           # "Doble clic" para Windows
├── arrancar-sistema.sh            # "Doble clic" para Linux/Mac
├── detener-sistema.bat
├── detener-sistema.sh
└── ver-logs.sh
```

### Cómo compilar los .jar

Desde la raíz del proyecto (con código fuente):

```bash
./mvnw clean package -DskipTests
```

Luego copiar los archivos `target/*.jar` de cada módulo a la carpeta `apps/`.

### Cómo ejecutar

**Opción 1 — Un clic:**
- Windows: Doble clic en `arrancar-sistema.bat`
- Linux/Mac: `./arrancar-sistema.sh`

**Opción 2 — Manual:**
```bash
docker compose up -d
```

### Orden de arranque

Los contenedores se levantan en este orden automáticamente:

1. **MySQL** — Base de datos (espera hasta que esté saludable)
2. **Eureka Server** — Service registry
3. **Microservicios de negocio** (ms-03 a ms-12) — Se registran en Eureka
4. **API Gateway** — Punto de entrada único

### Perfiles Docker

Cada microservicio tiene un perfil `docker` que sobrescribe las conexiones:

| Propiedad | Desarrollo (default) | Docker |
|-----------|---------------------|--------|
| `spring.datasource.url` | `localhost:3306/db_xxx` | `mysql-db:3306/db_xxx` |
| `eureka.client.service-url.defaultZone` | `localhost:8761/eureka/` | `eureka-server:8761/eureka/` |
| `spring.datasource.password` | (vacío) | `root` |

Se activa automáticamente vía `SPRING_PROFILES_ACTIVE=docker` en el `docker-compose.yml`.

### Comandos útiles

```bash
# Ver logs en vivo
docker compose logs -f

# Ver estado de contenedores
docker compose ps

# Detener sistema (conserva datos)
docker compose down

# Detener sistema y borrar datos
docker compose down -v
```

> **Nota:** `docker compose down -v` elimina los volúmenes de MySQL, perdiendo todos los datos. Usar con precaución.

---

> Este proyecto es un sistema completo de microservicios con Spring Boot, siguiendo las mejores prácticas de arquitectura distribuida con service discovery, API gateway, autenticación JWT y bases de datos separadas por servicio.