# Plataforma de Cursos Online - Documentación Técnica

## 1. Idea General del Sistema

Plataforma integral de aprendizaje online (backend) tipo "mini-Coursera" donde los usuarios pueden:

- Registrarse y gestionar su perfil personal
- Los estudiantes se inscriben en cursos disponibles
- Consumir contenido educativo a través de lecciones (texto e imágenes)
- Rendir evaluaciones para validar el aprendizaje
- Obtener certificados automáticos al aprobar cursos

---

## 2. Arquitectura del Sistema

### 2.1 Componentes de Infraestructura

| Componente | Puerto | Descripción |
|------------|--------|-------------|
| Eureka Server | 8761 | Service Registry - todos los microservicios se registran aquí |
| API Gateway | 8080 | Entry point - enruta peticiones a los microservicios |

### 2.2 Stack Tecnológico

- **Framework**: Spring Boot 3.x
- **Java**: JDK 17+
- **Base de Datos**: MySQL 8.0 (XAMPP)
- **Puerto MySQL**: 3307 (configuración requerida)
- **Comunicación**: REST + Feign Client
- **Autenticación**: JWT (JSON Web Tokens)
- **Gestor de Dependencias**: Maven

---

## 3. Roles del Sistema

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| ADMIN | Gestor de la plataforma | Gestión completa de usuarios, categorías, reportes |
| INSTRUCTOR | Creador de contenido | Crear/editar cursos, lecciones, evaluaciones propias |
| STUDENT | Consumidor de contenido | Inscribirse, estudiar, rendrir exámenes, descargar certificados |

---

## 4. Microservicios (10 en total)

### 4.1 Servicios de Infraestructura (3 obligatorios)

#### 4.1.1 auth-service (Puerto: 8081)
**Responsabilidad**: Autenticación y generación de tokens JWT.

**Responsabilidades técnicas**:
- Validar credenciales (email/password)
- Generar tokens JWT con información del usuario
- Validar tokens JWT entrantes
- Gestionar logout (blacklist de tokens)

**Base de datos**: `db_auth` (tabla: users con passwords encriptadas)

**Endpoints**:
```
POST /auth/login          - Login con credenciales
POST /auth/register       - Registro de nuevo usuario
GET  /auth/validate       - Validar token JWT
POST /auth/refresh        - Renovar token expirado
```

---

#### 4.1.2 user-service (Puerto: 8082)
**Responsabilidad**: Gestión de datos personales de usuarios.

**Responsabilidades técnicas**:
- CRUD de usuarios (perfil, datos personales)
- Actualizar información de perfil
- Consultar datos de usuario por ID
- Gestionar estado (activo/inactivo)

**Base de datos**: `db_users` (tabla: user_profile)

**Endpoints**:
```
POST   /users              - Crear usuario
GET    /users/{id}         - Obtener usuario por ID
PUT    /users/{id}         - Actualizar usuario
DELETE /users/{id}         - Eliminar usuario (soft delete)
GET    /users/email/{email} - Buscar por email
```

---

#### 4.1.3 security-service (Puerto: 8083)
**Responsabilidad**: Gestión de roles, permisos y autorización.

**Responsabilidades técnicas**:
- Asignar roles a usuarios (ADMIN, INSTRUCTOR, STUDENT)
- Validar permisos por rol para acceder a recursos
- Verificar acceso a endpoints específicos
- Sincronizar roles con otros servicios vía Feign

**Base de datos**: `db_security` (tablas: roles, permissions, user_roles)

**Endpoints**:
```
POST   /security/roles             - Crear rol
GET    /security/roles            - Listar roles
POST   /security/assign-role      - Asignar rol a usuario
GET    /security/user/{id}/roles  - Obtener roles de usuario
GET    /security/validate         - Validar permisos (usado por Gateway)
```

---

### 4.2 Servicios de Negocio (7 servicios)

#### 4.2.1 course-service (Puerto: 8084)
**Responsabilidad**: Gestión del catálogo de cursos y sistema de reviews/ratings.

**Responsabilidades técnicas**:
- CRUD de cursos (título, descripción, precio, imagen)
- Publicar/despublicar cursos
- Buscar cursos por título, categoría
- Sistema de reviews y ratings (1-5 estrellas)
- Promedio de rating calculado automáticamente
- Gestión de estado del curso (BORRADOR, PUBLISHED, ARCHIVED)

**Base de datos**: `db_courses` (tablas: courses, reviews)

**Relaciones con otros servicios**:
- Llama a category-service (vía Feign) para validar categoría
- Llama a user-service (vía Feign) para obtener datos del instructor

**Endpoints**:
```
POST   /courses                           - Crear curso (INSTRUCTOR/ADMIN)
GET    /courses                           - Listar cursos (con filtros)
GET    /courses/{id}                      - Detalle de curso
PUT    /courses/{id}                      - Actualizar curso
DELETE /courses/{id}                      - Eliminar curso
GET    /courses/category/{categoryId}    - Cursos por categoría
GET    /courses/instructor/{id}           - Cursos de un instructor
POST   /courses/{id}/reviews              - Agregar review
GET    /courses/{id}/reviews              - Listar reviews de curso
GET    /courses/{id}/rating               - Obtener promedio rating
```

---

#### 4.2.2 lesson-service (Puerto: 8085)
**Responsabilidad**: Gestión del contenido de las lecciones.

**Responsabilidades técnicas**:
- CRUD de lecciones (título, contenido texto, imágenes)
- Associar lecciones a cursos
- Ordenar lecciones dentro de un curso
- Contenido enriquecido (texto + imágenes)
- Gestionar estado (BORRADOR, PUBLISHED)

**Base de datos**: `db_lessons` (tabla: lessons)

**Relaciones con otros servicios**:
- Llama a course-service (vía Feign) para validar que el curso existe

**Endpoints**:
```
POST   /lessons                    - Crear lección
GET    /lessons/{id}               - Obtener lección
PUT    /lessons/{id}               - Actualizar lección
DELETE /lessons/{id}               - Eliminar lección
GET    /lessons/course/{courseId} - Listar lecciones de un curso
PUT    /lessons/{id}/order        - Actualizar orden de lección
```

---

#### 4.2.3 category-service (Puerto: 8086)
**Responsabilidad**: Gestión de categorías para organizar cursos.

**Responsabilidades técnicas**:
- CRUD de categorías (nombre, descripción, icono)
- Categorías jerárquicas (padre/hijo)
- Listar categorías disponibles
- Validar existencia de categoría

**Base de datos**: `db_categories` (tabla: categories)

**Endpoints**:
```
POST   /categories          - Crear categoría (ADMIN)
GET    /categories         - Listar todas las categorías
GET    /categories/{id}     - Obtener categoría
PUT    /categories/{id}    - Actualizar categoría
DELETE /categories/{id}    - Eliminar categoría
GET    /categories/{id}/courses - Cursos en esta categoría
```

---

#### 4.2.4 enrollment-service (Puerto: 8087)
**Responsabilidad**: Gestión de inscripciones de estudiantes a cursos.

**Responsabilidades técnicas**:
- Inscribir estudiante en curso
- Validar que el estudiante existe (llama a user-service)
- Validar que el curso existe y está publicado (llama a course-service)
- Gestionar estado de inscripción (PENDING, ACTIVE, COMPLETED, CANCELLED)
- Cancelar inscripción
- Listar cursos donde está inscrito un estudiante

**Base de datos**: `db_enrollments` (tabla: enrollments)

**Relaciones con otros servicios**:
- Llama a user-service (vía Feign) para validar estudiante
- Llama a course-service (vía Feign) para validar curso
- progress-service consulta enrollment para saber qué cursos tiene activos

**Endpoints**:
```
POST   /enrollments                      - Inscribir en curso
GET    /enrollments/{id}                 - Obtener inscripción
GET    /enrollments/student/{id}        - Cursos del estudiante
GET    /enrollments/course/{courseId}   - Estudiantes inscritos
PUT    /enrollments/{id}/cancel          - Cancelar inscripción
GET    /enrollments/check?userId=X&courseId=Y - Verificar inscripción activa
```

---

#### 4.2.5 progress-service (Puerto: 8088)
**Responsabilidad**: Seguimiento del avance del estudiante en cada curso.

**Responsabilidades técnicas**:
- Registrar progreso de lecciones vistas
- Calcular porcentaje de avance (0-100%)
- Validar secuencialidad (lección 2 solo可见 después de terminar lección 1)
- Historial de progreso por curso
- Marcar lección como completada

**Base de datos**: `db_progress` (tabla: progress)

**Relaciones con otros servicios**:
- Llama a lesson-service (vía Feign) para validar que la lección existe
- Llama a enrollment-service (vía Feign) para verificar que el estudiante está inscrito

**Endpoints**:
```
POST   /progress                    - Marcar lección como completada
GET    /progress/course/{courseId}/user/{userId} - Ver progreso en curso
GET    /progress/user/{userId}      - Todos los progresos del usuario
PUT    /progress/reset/course/{courseId}/user/{userId} - Reiniciar progreso
GET    /progress/percentage/course/{courseId}/user/{userId} - Porcentaje actual
```

**Lógica de secuencialidad**:
- Al marcar lección N como completada, automáticamente la lección N+1 queda disponible
- Solo se puede acceder a lecciones en orden secuencial
- El estudiante debe completar la lección anterior para desbloquear la siguiente

---

#### 4.2.6 evaluation-service (Puerto: 8089)
**Responsabilidad**: Administración de evaluaciones, exámenes y calificaciones.

**Responsabilidades técnicas**:
- CRUD de evaluaciones por curso
- Banco de preguntas (opción múltiple, verdadero/falso)
- Crear exámenes asociando preguntas
- Rendir examen y calcular nota
- Historial de notas por estudiante
- Cálculo de promedio automático
- Definir nota mínima para aprobar (ej: 70%)

**Base de datos**: `db_evaluations` (tablas: evaluations, questions, exam_answers, grades)

**Relaciones con otros servicios**:
- Llama a course-service (vía Feign) para validar curso
- Llama a enrollment-service (vía Feign) para verificar inscripción activa

**Endpoints**:
```
POST   /evaluations                    - Crear evaluación (INSTRUCTOR)
GET    /evaluations/{id}               - Obtener evaluación
GET    /evaluations/course/{courseId} - Evaluaciones de un curso
POST   /evaluations/{id}/questions     - Agregar pregunta
POST   /evaluations/{id}/submit        - Entregar examen
GET    /grades/user/{userId}           - Notas del usuario
GET    /grades/user/{userId}/course/{courseId} - Nota específica
GET    /grades/average/user/{userId}   - Promedio general del usuario
```

**Estructura de pregunta**:
```json
{
  "questionText": "¿Qué es Spring Boot?",
  "questionType": "MULTIPLE_CHOICE",
  "options": ["A framework", "Un lenguaje", "Una base de datos"],
  "correctAnswer": "A framework"
}
```

---

#### 4.2.7 certificate-service (Puerto: 8090)
**Responsabilidad**: Generación y validación de certificados.

**Responsabilidades técnicas**:
- Generar certificado cuando el estudiante aprueba (nota >= mínimo)
- Código único de verificación por certificado
- Datos del curso, estudiante, fecha de emisión
- Validar certificado por código
- Descargar certificado (PDF básico con iText o similar)

**Base de datos**: `db_certificates` (tabla: certificates)

**Relaciones con otros servicios**:
- Llama a evaluation-service (vía Feign) para verificar nota mínima aprobada
- Llama a course-service (vía Feign) para obtener datos del curso
- Llama a user-service (vía Feign) para obtener datos del estudiante

**Endpoints**:
```
POST   /certificates/generate          - Generar certificado (auto al aprobar)
GET    /certificates/{id}             - Obtener certificado
GET    /certificates/user/{userId}    - Certificados del usuario
GET    /certificates/verify/{code}    - Verificar certificado por código
GET    /certificates/{id}/download    - Descargar PDF del certificado
```

**Lógica de generación**:
1. El estudiante aprueba la evaluación (nota >= 70%)
2. Se verifica que no exista certificado previo para ese curso
3. Se genera código único (UUID)
4. Se crea registro en bd con los datos del curso y estudiante

---

## 5. Bases de Datos (Database per Microservice)

| Microservicio | Base de Datos | Puerto MySQL | Tablas |
|---------------|---------------|--------------|--------|
| auth-service | db_auth | 3307 | users |
| user-service | db_users | 3307 | user_profile |
| security-service | db_security | 3307 | roles, permissions, user_roles |
| course-service | db_courses | 3307 | courses, reviews |
| lesson-service | db_lessons | 3307 | lessons |
| category-service | db_categories | 3307 | categories |
| enrollment-service | db_enrollments | 3307 | enrollments |
| progress-service | db_progress | 3307 | progress |
| evaluation-service | db_evaluations | 3307 | evaluations, questions, grades |
| certificate-service | db_certificates | 3307 | certificates |

**Configuración Application.properties (ejemplo):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_auth
spring.datasource.username=root
spring.datasource.password=
```

---

## 6. Flujo Principal del Sistema

### 6.1 Flujo: Inscripción y Estudio

```
1. AUTH: Usuario hace login → recibe JWT token
2. COURSE: Explora catálogo de cursos → ve detalles
3. CATEGORY: Filtra cursos por categoría
4. ENROLLMENT: Se inscriibe en curso (valida user + course)
5. LESSON: Accede a lecciones del curso (texto/imágenes)
6. PROGRESS: Marca lecciones como completadas (secuencial)
7. EVALUATION: Rendir examen → recibe calificación
8. CERTIFICATE: Si aprueba → certificado automático
```

### 6.2 Flujo: Revisión de Curso

```
1. STUDENT: Completa el curso
2. COURSE: Envía review (rating 1-5 + comentario)
3. COURSE: Se calcula promedio de ratings
4. COURSE: Otros estudiantes ven rating promedio
```

---

## 7. Comunicación entre Microservicios

### 7.1 Comunicación Síncrona (Feign Client)

| Servicio que llama | Servicio que responde | Propósito |
|-------------------|----------------------|-----------|
| enrollment-service | user-service | Validar que el estudiante existe |
| enrollment-service | course-service | Validar que el curso existe y está publicado |
| progress-service | lesson-service | Validar que la lección existe |
| progress-service | enrollment-service | Verificar inscripción activa |
| certificate-service | evaluation-service | Verificar nota mínima aprobada |
| certificate-service | course-service | Obtener datos del curso |
| certificate-service | user-service | Obtener datos del estudiante |
| course-service | category-service | Validar categoría del curso |
| course-service | user-service | Obtener datos del instructor |
| evaluation-service | course-service | Validar curso de la evaluación |
| evaluation-service | enrollment-service | Verificar inscripción activa |

### 7.2 Estructura de Feign Client

Cada servicio que necesita comunicarse con otro debe tener:

```
src/main/java/com/example/servicio/
├── controller/
├── service/
├── repository/
└── client/                          ← Carpeta para Feign Clients
    ├── UserClient.java              ← Interfaz para user-service
    └── CourseClient.java            ← Interfaz para course-service
```

**Ejemplo de Feign Client:**

```java
@FeignClient(name = "user-service", url = "http://localhost:8082")
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
}
```

---

## 8. Seguridad y Autenticación

### 8.1 Flujo de Autenticación

1. Usuario envía credenciales a `auth-service`
2. `auth-service` valida contra `db_auth`
3. Genera JWT token con: userId, email, role
4. Usuario incluye token en header: `Authorization: Bearer <token>`
5. `security-service` valida token y permisos en cada request

### 8.2 Configuración de Seguridad

**application.properties del Gateway:**
```properties
spring.cloud.gateway.routes[0].uri=lb://auth-service
spring.cloud.gateway.routes[0].predicates=Path=/auth/**
```

**Rutas públicas vs protegidas:**
- `/auth/**` - Público (login, register)
- `/courses/**` - Protegido (requiere JWT)
- `/admin/**` - Solo ADMIN

### 8.3 Roles y Permisos

| Endpoint | Método | Rol requerido |
|----------|--------|---------------|
| /courses | POST | INSTRUCTOR, ADMIN |
| /courses/{id} | DELETE | INSTRUCTOR (dueño), ADMIN |
| /enrollments | POST | STUDENT |
| /evaluations/*/submit | POST | STUDENT |
| /certificates/*/generate | POST | STUDENT (auto) |
| /categories | POST | ADMIN |

---

## 9. Características Adicionales Implementadas

### 9.1 Sistema de Progreso
- Porcentaje de avance por curso (0-100%)
- Lecciones secuenciales (no puedes ver Lección 2 sin terminar Lección 1)
- Reinicio de progreso disponible

### 9.2 Sistema de Certificados
- Generación automática al aprobar (nota >= 70%)
- Código único de verificación
- Descarga en formato PDF

### 9.3 Sistema de Ratings/Reviews
- Rating de 1 a 5 estrellas por curso
- Comentarios de texto
- Promedio automático de rating

### 9.4 Tipos de Cursos
- Cursos gratuitos (precio = 0)
- Cursos de pago (precio > 0) - sin procesamiento real

---

## 10. Estructura de Proyecto General

```
PlataformaCursos/
├── eureka-server/           (Puerto 8761)
├── api-gateway/             (Puerto 8080)
├── auth-service/           (Puerto 8081)
├── user-service/           (Puerto 8082)
├── security-service/       (Puerto 8083)
├── course-service/         (Puerto 8084)
├── lesson-service/         (Puerto 8085)
├── category-service/       (Puerto 8086)
├── enrollment-service/     (Puerto 8087)
├── progress-service/       (Puerto 8088)
├── evaluation-service/     (Puerto 8089)
└── certificate-service/   (Puerto 8090)
```

---

## 11. Testing con Postman

### Colección de Endpoints Sugerida

**Autenticación:**
- POST /auth/login
- POST /auth/register
- GET /auth/validate

**Cursos:**
- POST /courses (crear curso)
- GET /courses (listar todos)
- GET /courses/{id} (detalle)
- POST /courses/{id}/reviews (agregar review)
- GET /courses/{id}/rating (ver rating)

**Inscripción:**
- POST /enrollments (inscribir)
- GET /enrollments/student/{id} (mis cursos)

**Progreso:**
- POST /progress (marcar completada)
- GET /progress/percentage/course/{courseId}/user/{userId}

**Evaluaciones:**
- POST /evaluations/{id}/submit (entregar examen)
- GET /grades/user/{userId}

**Certificados:**
- GET /certificates/user/{userId}
- GET /certificates/verify/{code}

---

## 12. Consideraciones Técnicas

### 12.1 Errores Comunes a Evitar
- ❌ No compartir tablas entre bases de datos
- ❌ No concentrar toda la lógica en un solo servicio
- ❌ No hacer CRUD básicos sin reglas de negocio
- ❌ No omitir validación de roles (estudiante no puede borrar curso)
- ❌ No usar Tomcat en Gateway (usar Netty con Spring Cloud Gateway)

### 12.2 Puerto MySQL
- Configurar en `application.properties`: `localhost:3307`
- MySQL debe estar corriendo en XAMPP o instalación nativa

### 12.3 Naming Convention
- Entidades: PascalCase (User, Course, Enrollment)
- Tablas: snake_case (user_profile, course_reviews)
- Endpoints: kebab-case (POST /auth/login)
- Properties: kebab-case (spring.datasource.url)

---

## 13. Cronograma Sugerido (Hasta 18 Mayo)

| Semana | Servicios | Objetivo |
|--------|-----------|----------|
| Semana 1 | Eureka + Gateway + auth + user + security | Infraestructura base funcionando |
| Semana 2 | category + course + lesson | Catálogo y contenido de cursos |
| Semana 3 | enrollment + progress | Inscripción y seguimiento de progreso |
| Semana 4 | evaluation + certificate | Exámenes y generación de certificados |
| Semana 5 | Pruebas | Testing completo en Postman, ajustes |

---

*Documento generado para proyecto universitario - Spring Boot Microservices*