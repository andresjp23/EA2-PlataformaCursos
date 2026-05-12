# DOCUMENTACIÓN DE ENDPOINTS - Plataforma de Cursos Online

**Última actualización**: Mayo 6, 2026  
**Versión**: 1.0  
**Estado**: Fase 1 - Infraestructura Base Implementada

---

## ÍNDICE DE CONTENIDOS

1. [Introducción](#introducción)
2. [Arquitectura de Microservicios](#arquitectura-de-microservicios)
3. [Servicios Implementados](#servicios-implementados)
4. [Servicios Pendientes](#servicios-pendientes)
5. [Endpoints Disponibles](#endpoints-disponibles)
6. [Flujos de Autenticación](#flujos-de-autenticación)
7. [Estructura de Carpetas](#estructura-de-carpetas)
8. [Convenciones y Estándares](#convenciones-y-estándares)

---

## INTRODUCCIÓN

Este documento documenta todos los endpoints API disponibles en la plataforma de cursos online. El sistema está construido con **Spring Boot 3.x** usando una arquitectura de **microservicios** con **Netflix Eureka** para el service registry y **Spring Cloud Gateway** como punto de entrada único.

### Stack Tecnológico
- **Framework**: Spring Boot 3.x / 4.0.6
- **Java**: JDK 21
- **Base de Datos**: MySQL 8.0
- **Autenticación**: JWT (JSON Web Tokens)
- **Comunicación**: REST API + Feign Client
- **Service Registry**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

---

## ARQUITECTURA DE MICROSERVICIOS

### Componentes de Infraestructura

| Componente | Puerto | Estado | Descripción |
|------------|--------|--------|-------------|
| **Eureka Server** | 8761 | ✅ Implementado | Service Registry - Registro de todos los microservicios |
| **API Gateway** | 8080 | ✅ Implementado | Entry point único - Enruta peticiones a los servicios |

### Configuración del Gateway

El API Gateway actúa como proxy reverso y enruta las peticiones a los microservicios correspondientes según el path:

```properties
# Ruta para Auth Service
spring.cloud.gateway.routes[0].id=auth-service
spring.cloud.gateway.routes[0].uri=lb://auth-service
spring.cloud.gateway.routes[0].predicates=Path=/auth/**

# Ruta para User Service (Pendiente)
spring.cloud.gateway.routes[1].id=user-service
spring.cloud.gateway.routes[1].uri=lb://user-service
spring.cloud.gateway.routes[1].predicates=Path=/users/**
```

---

## SERVICIOS IMPLEMENTADOS

### 1. MS-01-EUREKA (Eureka Server)
**Estado**: ✅ Implementado  
**Puerto**: 8761  
**Responsabilidad**: Service Registry

#### Función
- Actúa como registro central de todos los microservicios
- Los servicios se registran automáticamente al iniciar
- Permite el descubrimiento de servicios entre microservicios
- Detecta automáticamente cuando un servicio se desconecta

#### Endpoints del Eureka Dashboard
- **GET** `http://localhost:8761/` - Dashboard de Eureka (interfaz web)

#### Configuración
```properties
spring.application.name=ms-01-eureka
server.port=8761
eureka.instance.hostname=localhost
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.server.enable-self-preservation=false
```

#### Servicios Registrados Esperados
Cuando todos los microservicios estén en funcionamiento, aparecerán en el dashboard:
- auth-service (8081)
- user-service (8082)
- security-service (8083)
- course-service (8084)
- lesson-service (8085)
- category-service (8086)
- enrollment-service (8087)
- progress-service (8088)
- evaluation-service (8089)
- certificate-service (8090)

---

### 2. MS-02-GATEWAY (API Gateway)
**Estado**: ✅ Implementado  
**Puerto**: 8080  
**Responsabilidad**: Enrutamiento de peticiones y circuit breaker

#### Función
- Punto de entrada único a todos los microservicios
- Enruta peticiones a los servicios según el path
- Implementa circuit breaker con Resilience4J
- Descubre servicios automáticamente vía Eureka

#### Rutas Configuradas
```properties
# Auth Service
/auth/** → http://localhost:8081/auth/**

# User Service (cuando esté listo)
/users/** → http://localhost:8082/users/**
```

#### Cómo Usar el Gateway
Todas las peticiones deben hacerse a través del gateway:

```bash
# En lugar de:
POST http://localhost:8081/auth/login

# Usar:
POST http://localhost:8080/auth/login
```

#### Configuración
```properties
spring.application.name=ms-02-gateway
server.port=8080
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
spring.cloud.gateway.routes[0].id=auth-service
spring.cloud.gateway.routes[0].uri=lb://auth-service
spring.cloud.gateway.routes[0].predicates=Path=/auth/**
```

---

### 3. MS-03-AUTH-SERVICE (Auth Service)
**Estado**: ✅ Implementado  
**Puerto**: 8081  
**Responsabilidad**: Autenticación y generación de JWT

#### Función
- Valida credenciales de usuarios
- Genera tokens JWT
- Gestiona registro de nuevos usuarios
- Encripta contraseñas con BCrypt

#### Base de Datos
- **Nombre**: db_auth
- **Tabla**: users
- **Conexión**: jdbc:mysql://localhost:3306/db_auth

#### Estructura de la Tabla Users
```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) DEFAULT 'STUDENT',
  enabled BOOLEAN DEFAULT TRUE
);
```

#### Estructura de Clases

```
src/main/java/com/plataforma/ms_03_auth_service/
├── config/
│   ├── JwtUtil.java          ← Generación de tokens JWT
│   └── SecurityConfig.java   ← Configuración de seguridad
├── controllers/
│   ├── AuthController.java   ← Endpoints de autenticación
│   ├── LoginRequest.java     ← DTO para login
│   └── RegisterRequest.java  ← DTO para registro
├── models/
│   └── User.java             ← Entidad Usuario
├── repositories/
│   └── UserRepository.java   ← Acceso a datos
├── services/
│   ├── DataInitializer.java  ← Inicialización de datos
│   └── UserDetailServiceImpl.java ← Detalles de usuario
└── Ms03AuthServiceApplication.java
```

---

## ENDPOINTS DISPONIBLES

### AUTENTICACIÓN - /auth/**

**Base URL**: `http://localhost:8080/auth` (a través del Gateway)  
**Servicio Backend**: auth-service (8081)  
**Autenticación Requerida**: No (rutas públicas)

#### 1. POST /auth/login
**Descripción**: Autentica un usuario y retorna un token JWT

**Método HTTP**: `POST`

**URL Completa**:
```
POST http://localhost:8080/auth/login
```

**Headers**:
```
Content-Type: application/json
```

**Body (Request)**:
```json
{
  "email": "usuario@example.com",
  "password": "miContraseña123"
}
```

**Parámetros**:

| Parámetro | Tipo | Ubicación | Obligatorio | Descripción |
|-----------|------|-----------|-------------|-------------|
| email | String | Body | Sí | Email registrado del usuario |
| password | String | Body | Sí | Contraseña sin encriptar |

**Respuesta Exitosa (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "STUDENT",
  "userId": 1
}
```

**Respuesta con Error (401 Unauthorized)**:
```json
{
  "error": "Bad credentials"
}
```

**Ejemplo con cURL**:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@example.com",
    "password": "miContraseña123"
  }'
```

**Ejemplo con Postman**:
1. Seleccionar método: `POST`
2. URL: `http://localhost:8080/auth/login`
3. Tab "Body" → "raw" → seleccionar "JSON"
4. Pegar el JSON del body
5. Hacer clic en "Send"

**Notas Técnicas**:
- Las contraseñas se validan contra hashes BCrypt
- El token JWT tiene expiración configurada en `jwt.expiration=86400000` (24 horas)
- El token debe incluirse en peticiones futuras: `Authorization: Bearer <token>`

---

#### 2. POST /auth/register
**Descripción**: Registra un nuevo usuario en el sistema

**Método HTTP**: `POST`

**URL Completa**:
```
POST http://localhost:8080/auth/register
```

**Headers**:
```
Content-Type: application/json
```

**Body (Request)**:
```json
{
  "email": "nuevouser@example.com",
  "password": "MiPassword123",
  "role": "STUDENT"
}
```

**Parámetros**:

| Parámetro | Tipo | Ubicación | Obligatorio | Descripción |
|-----------|------|-----------|-------------|-------------|
| email | String | Body | Sí | Email único para el nuevo usuario |
| password | String | Body | Sí | Contraseña (sin encriptar, se guarda con BCrypt) |
| role | String | Body | No | Rol del usuario: STUDENT, INSTRUCTOR, ADMIN. Default: STUDENT |

**Respuesta Exitosa (200 OK)**:
```json
{
  "message": "Usuario registrado exitosamente"
}
```

**Respuesta con Error - Email Duplicado (400 Bad Request)**:
```json
{
  "message": "Email ya registrado"
}
```

**Ejemplo con cURL**:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nuevouser@example.com",
    "password": "MiPassword123",
    "role": "STUDENT"
  }'
```

**Validaciones**:
- El email debe ser único en la tabla users
- La contraseña se encripta automáticamente con BCrypt
- Si no se especifica rol, se asigna "STUDENT" por defecto
- El usuario se crea con `enabled=true`

**Flujo Implementado**:
1. Verifica si el email ya existe
2. Si existe, retorna error 400
3. Si no existe, crea nuevo usuario
4. Encripta la contraseña
5. Guarda en base de datos
6. Retorna mensaje de éxito

---

#### 3. GET /auth/validate (Pendiente de Implementación)
**Descripción**: Valida un token JWT

**Método HTTP**: `GET`

**URL Completa**:
```
GET http://localhost:8080/auth/validate?token=<jwt_token>
```

**Parámetros Query**:

| Parámetro | Tipo | Obligatorio | Descripción |
|-----------|------|-------------|-------------|
| token | String | Sí | Token JWT a validar |

**Respuesta Esperada (200 OK)**:
```json
{
  "valid": true,
  "email": "usuario@example.com",
  "userId": 1
}
```

**Estado**: 🔴 No Implementado Aún

---

#### 4. POST /auth/refresh (Pendiente de Implementación)
**Descripción**: Renueva un token JWT expirado

**Método HTTP**: `POST`

**URL Completa**:
```
POST http://localhost:8080/auth/refresh
```

**Headers**:
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Body (Request)**:
```json
{
  "refreshToken": "token_para_refrescar"
}
```

**Respuesta Esperada (200 OK)**:
```json
{
  "token": "nuevo_jwt_token",
  "expiresIn": 86400000
}
```

**Estado**: 🔴 No Implementado Aún

---

### USUARIOS - /users/** (Pendiente)

**Base URL**: `http://localhost:8080/users`  
**Servicio Backend**: user-service (8082 - Aún no existe)  
**Autenticación Requerida**: Sí

#### Endpoints Planeados

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /users | Crear usuario |
| GET | /users/{id} | Obtener usuario por ID |
| PUT | /users/{id} | Actualizar usuario |
| DELETE | /users/{id} | Eliminar usuario (soft delete) |
| GET | /users/email/{email} | Buscar usuario por email |

**Estado**: 🔴 Servicio No Implementado Aún (Fase 2)

---

## FLUJOS DE AUTENTICACIÓN

### Flujo 1: Registro de Nuevo Usuario

```
1. Cliente envía POST /auth/register
   ├─ email: "usuario@example.com"
   ├─ password: "MiPassword123"
   └─ role: "STUDENT"

2. AuthController.register() procesa
   ├─ Verifica si email existe
   ├─ Si existe → retorna error 400
   └─ Si NO existe:
      ├─ Crea objeto User
      ├─ Encripta password con BCrypt
      ├─ Asigna rol (default: STUDENT)
      ├─ Guarda en db_auth.users
      └─ Retorna success

3. Respuesta: {message: "Usuario registrado exitosamente"}
```

### Flujo 2: Login y Obtención de Token

```
1. Cliente envía POST /auth/login
   ├─ email: "usuario@example.com"
   └─ password: "MiPassword123"

2. AuthController.login() procesa
   ├─ Obtiene credenciales
   ├─ Usa AuthenticationManager
   └─ Valida contra BCrypt en BD

3. Si validación exitosa:
   ├─ Obtiene objeto User de SecurityContext
   ├─ Genera JWT con JwtUtil
   ├─ Token contiene: email y fecha expiración
   ├─ Incluye rol del usuario
   └─ Incluye userId

4. Respuesta:
   {
     "token": "eyJhbGciOiJIUzI1NiIs...",
     "role": "STUDENT",
     "userId": 1
   }

5. Cliente almacena token (localStorage/sessionStorage)

6. Peticiones Futuras:
   ├─ Header: Authorization: Bearer <token>
   └─ Gateway/Security valida el token
```

### Flujo 3: Petición Autenticada (Futuro)

```
1. Cliente hace petición a endpoint protegido
   GET /users/123
   Headers:
     Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

2. API Gateway recibe
   ├─ Extrae token del header
   └─ Lo envía a security-service para validar

3. Security-Service valida
   ├─ Verifica firma JWT
   ├─ Verifica expiración
   ├─ Verifica permisos del usuario
   └─ Retorna OK o 401/403

4. Si válido:
   ├─ Gateway permite que pase
   └─ Petición llega al servicio

5. Si inválido:
   ├─ Gateway retorna 401 (token inválido)
   └─ O 403 (sin permisos suficientes)
```

---

## ESTRUCTURA DE CARPETAS

### Organización General del Proyecto

```
PlataformaCursos/                    ← Raíz del proyecto
│
├── .github/                         ← Configuración de GitHub
│
├── .vscode/                         ← Configuración de VS Code
│
├── docs/                            ← Documentación del proyecto
│   ├── PROYECTO_PLATAFORMA_CURSOS.md
│   ├── PLAN_DESARROLLO.md
│   ├── INDICE_DOCUMENTACION.md
│   └── ENDPOINTS_DOCUMENTACION.md   ← Este archivo
│
├── ms-01-eureka/                    ← Microservicio Eureka Server
│   ├── src/main/java/com/plataforma/ms_01_eureka/
│   │   └── Ms01EurekaApplication.java
│   └── src/main/resources/
│       └── application.properties
│
├── ms-02-gateway/                   ← Microservicio API Gateway
│   ├── src/main/java/com/plataforma/ms_02_gateway/
│   │   └── Ms02GatewayApplication.java
│   └── src/main/resources/
│       └── application.properties
│
└── ms-03-auth-service/              ← Microservicio Auth Service
    ├── src/main/java/com/plataforma/ms_03_auth_service/
    │   ├── config/
    │   │   ├── JwtUtil.java
    │   │   └── SecurityConfig.java
    │   ├── controllers/
    │   │   ├── AuthController.java
    │   │   ├── LoginRequest.java
    │   │   └── RegisterRequest.java
    │   ├── models/
    │   │   └── User.java
    │   ├── repositories/
    │   │   └── UserRepository.java
    │   ├── services/
    │   │   ├── DataInitializer.java
    │   │   └── UserDetailServiceImpl.java
    │   └── Ms03AuthServiceApplication.java
    │
    ├── src/main/resources/
    │   └── application.properties
    │
    ├── src/test/java/
    │   └── Ms03AuthServiceApplicationTests.java
    │
    ├── pom.xml                       ← Dependencias Maven
    └── target/                       ← Compilados (ignorar)
```

### Estructura Interna de Microservicio (Auth Service como Ejemplo)

```
ms-03-auth-service/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/plataforma/ms_03_auth_service/
│   │   │       │
│   │   │       ├── config/                    ← Configuración
│   │   │       │   ├── JwtUtil.java           ← Utilidades JWT
│   │   │       │   └── SecurityConfig.java    ← Configuración Spring Security
│   │   │       │
│   │   │       ├── controllers/               ← Controladores REST
│   │   │       │   ├── AuthController.java    ← Endpoints /auth/**
│   │   │       │   ├── LoginRequest.java      ← DTO para login
│   │   │       │   └── RegisterRequest.java   ← DTO para registro
│   │   │       │
│   │   │       ├── models/                    ← Entidades JPA
│   │   │       │   └── User.java              ← Entidad User
│   │   │       │
│   │   │       ├── repositories/              ← Acceso a datos (JPA)
│   │   │       │   └── UserRepository.java    ← CRUD de User
│   │   │       │
│   │   │       ├── services/                  ← Lógica de negocio
│   │   │       │   ├── DataInitializer.java   ← Inicialización de datos
│   │   │       │   └── UserDetailServiceImpl.java ← Integración Spring Security
│   │   │       │
│   │   │       ├── client/                    ← Feign Clients (futuro)
│   │   │       │   └── (vacío por ahora)
│   │   │       │
│   │   │       └── Ms03AuthServiceApplication.java ← Clase principal Spring Boot
│   │   │
│   │   └── resources/
│   │       └── application.properties         ← Configuración
│   │
│   └── test/
│       └── java/
│           └── Ms03AuthServiceApplicationTests.java ← Tests
│
├── pom.xml                                    ← Dependencias Maven
│
├── mvnw / mvnw.cmd                            ← Maven Wrapper
│
├── target/                                    ← Compilados (ignorar)
│
└── HELP.md                                    ← Help generado por Spring
```

### Patrón de Carpetas para Futuros Microservicios

Todos los microservicios seguirán esta estructura estándar:

```
ms-XX-service-name/
└── src/main/java/com/plataforma/ms_XX_service_name/
    ├── config/                    ← Configuración, beans, utilidades
    ├── controllers/               ← Controladores REST
    ├── services/                  ← Lógica de negocio
    ├── repositories/              ← Acceso a datos (JPA/Hibernate)
    ├── models/                    ← Entidades JPA (Entity)
    ├── dto/                       ← Data Transfer Objects (futuro)
    ├── client/                    ← Feign Clients (futuro)
    ├── exception/                 ← Excepciones personalizadas (futuro)
    └── MsXXServiceNameApplication.java  ← Clase principal
```

---

## CONVENCIONES Y ESTÁNDARES

### 1. Convenciones de Nombres

#### Java
- **Clases**: PascalCase
  - Ejemplos: `User`, `AuthController`, `JwtUtil`, `UserRepository`
  
- **Métodos**: camelCase
  - Ejemplos: `login()`, `generateToken()`, `findByEmail()`
  
- **Constantes**: UPPER_SNAKE_CASE
  - Ejemplos: `EXPIRATION_TIME`, `SECRET_KEY`

#### Archivos
- **DTOs**: `{Entidad}Request.java` o `{Entidad}Response.java`
  - Ejemplos: `LoginRequest.java`, `RegisterRequest.java`
  
- **Servicios**: `{Nombre}Service.java`
  - Ejemplo: `UserService.java`
  
- **Controladores**: `{Recurso}Controller.java`
  - Ejemplo: `AuthController.java`

#### Bases de Datos
- **Tablas**: snake_case
  - Ejemplos: `users`, `user_profile`, `course_reviews`
  
- **Columnas**: snake_case
  - Ejemplos: `user_id`, `created_at`, `is_enabled`

#### URLs/Endpoints
- **Rutas**: kebab-case
  - Ejemplos: `/auth/login`, `/user-profile`, `/course-reviews`
  
- **Query params**: camelCase
  - Ejemplos: `?userId=123`, `?pageSize=10`

### 2. Estructura de Requests/Responses

#### LoginRequest.java
```java
@Data  // Lombok - genera getters, setters, equals, toString
public class LoginRequest {
    private String email;
    private String password;
}
```

#### Response (Map)
```json
{
  "token": "string",
  "role": "string",
  "userId": "number"
}
```

### 3. Códigos HTTP Usados

| Código | Nombre | Uso |
|--------|--------|-----|
| 200 | OK | Petición exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Error en los parámetros enviados |
| 401 | Unauthorized | No autenticado o token inválido |
| 403 | Forbidden | Autenticado pero sin permisos suficientes |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error en el servidor |

### 4. Autenticación con JWT

#### Estructura del Token JWT
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjMzMDM2MTAwLCJleHAiOjE2MzMxMjI1MDB9.signature
```

Tres partes separadas por `.`:
1. **Header**: Tipo de algoritmo (HS256)
2. **Payload**: Datos codificados (subject=email, issuedAt, expiration)
3. **Signature**: Firma digital

#### Cómo Usar el Token
```bash
# Header autorización
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

# En cURL
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..." http://localhost:8080/users/123
```

### 5. Configuración de Properties

#### jwt.secret
```properties
jwt.secret=PlataformaCursosSecretKey2026MuyLargaParaSeguridad
```

#### jwt.expiration
```properties
jwt.expiration=86400000  # 24 horas en milisegundos
```

### 6. Roles del Sistema

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| STUDENT | Estudiante | Puede inscribirse, estudiar, rendir exámenes |
| INSTRUCTOR | Instructor | Puede crear cursos, lecciones, evaluaciones |
| ADMIN | Administrador | Acceso total al sistema |

---

## RESUMEN DE ESTADO

### Fase 1: Infraestructura Base (EN CURSO ✅)

| Componente | Estado | Líneas de Código | Funcionalidad |
|------------|--------|-----------------|---------------|
| Eureka Server | ✅ Completo | ~50 | Service Registry funcional |
| API Gateway | ✅ Completo | ~100 | Routing a auth-service funcional |
| Auth Service | ✅ Completo | ~500 | Login y registro implementados |
| **Total Fase 1** | **✅ LISTA** | **~650** | Autenticación base funcionando |

### Fases Futuras

| Fase | Servicios | Estado | Estimado |
|------|-----------|--------|----------|
| Fase 2 | user-service, security-service | 🔴 Pendiente | ~5-7 días |
| Fase 3 | category-service, course-service, lesson-service | 🔴 Pendiente | ~7-10 días |
| Fase 4 | enrollment-service, progress-service | 🔴 Pendiente | ~5-7 días |
| Fase 5 | evaluation-service, certificate-service | 🔴 Pendiente | ~5-7 días |

---

## GUÍA RÁPIDA DE INICIO

### 1. Iniciar Eureka Server
```bash
cd /Users/ajordanp/MyProjects/PlataformaCursos/ms-01-eureka
./mvnw spring-boot:run

# Verificar: http://localhost:8761
```

### 2. Iniciar API Gateway
```bash
cd /Users/ajordanp/MyProjects/PlataformaCursos/ms-02-gateway
./mvnw spring-boot:run

# Verificar: http://localhost:8080
```

### 3. Iniciar Auth Service
```bash
cd /Users/ajordanp/MyProjects/PlataformaCursos/ms-03-auth-service
./mvnw spring-boot:run

# Verificar: http://localhost:8081 (a través de 8080)
```

### 4. Registrar un Usuario
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123",
    "role": "STUDENT"
  }'
```

### 5. Login y Obtener Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123"
  }'
```

---

## CONTACTO Y REFERENCIAS

- **Proyecto**: Plataforma de Cursos Online
- **Documentación Técnica Completa**: `/docs/PROYECTO_PLATAFORMA_CURSOS.md`
- **Plan de Desarrollo**: `/docs/PLAN_DESARROLLO.md`
- **GitHub**: [Repositorio del Proyecto]

---

**Documento generado**: Mayo 6, 2026  
**Versión**: 1.0  
**Autor**: Equipo de Desarrollo

