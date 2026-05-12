# Índice de Documentación - Plataforma de Cursos Online

## Descripción General
Este directorio contiene toda la documentación sobre la **Plataforma de Cursos Online**, un sistema de microservicios construido con Spring Boot y Spring Cloud.

---

## Documentos Disponibles

### 1. **RESUMEN_EJECUTIVO.txt** (COMIENZA AQUÍ)
**Tamaño:** 18 KB | **Formato:** Texto plano

**Contenido:**
- Visión general del proyecto
- Estado actual (3/12 servicios completados)
- Tecnologías y versiones utilizadas
- Flujo de autenticación implementado
- Recomendaciones y próximos pasos
- Comandos útiles

**A QUIÉN LE INTERESA:**
- Project managers
- Tech leads
- Nuevos desarrolladores que se integran al proyecto

**EMPIEZA AQUÍ** si necesitas entender rápidamente qué es el proyecto.

---

### 2. **ESTRUCTURA_PROYECTO.txt**
**Tamaño:** 27 KB | **Formato:** Texto plano con diagramas ASCII

**Contenido:**
- Estructura de carpetas completa
- Detalles de cada microservicio (MS-01, MS-02, MS-03)
- Configuración de cada servicio (application.properties)
- Componentes por capas (Controllers, Services, Repositories, Models)
- Tabla de dependencias y versiones
- Estructura de base de datos
- Rutas y endpoints
- Flujos de comunicación

**A QUIÉN LE INTERESA:**
- Desarrolladores backend
- Arquitectos de software
- DevOps engineers

**CONSULTA ESTO** para detalles técnicos específicos de los servicios.

---

### 3. **DIAGRAMA_ARQUITECTURA.txt**
**Tamaño:** 29 KB | **Formato:** Diagramas ASCII visuales

**Contenido:**
- Diagrama de arquitectura general
- Flujo de solicitudes HTTP
- Arquitectura interna de servicios (capas)
- Flujo de autenticación paso a paso
- Flujo de solicitud con JWT
- Mapa de puertos y servicios
- Dependencias entre servicios

**A QUIÉN LE INTERESA:**
- Arquitectos de software
- Nuevos desarrolladores
- Analistas de sistemas

**MIRA ESTO** si necesitas visualizar cómo funciona la arquitectura.

---

### 4. **PROYECTO_PLATAFORMA_CURSOS.md**
**Tamaño:** 19 KB | **Formato:** Markdown

**Contenido:**
- Idea general del sistema
- Arquitectura del sistema
- Roles del sistema (ADMIN, INSTRUCTOR, STUDENT)
- Descripción de todos los 12 microservicios
- Base de datos (Database per Microservice)
- Flujos principales del sistema
- Comunicación entre microservicios (Feign Client)
- Seguridad y autenticación
- Características adicionales
- Estructura de proyecto
- Plan de testing con Postman

**A QUIÉN LE INTERESA:**
- Product owners
- Business analysts
- Desarrolladores full-stack

**LEE ESTO** para entender los requisitos completos del sistema.

---

### 5. **PLAN_DESARROLLO.md**
**Tamaño:** 1.2 KB | **Formato:** Markdown

**Contenido:**
- Plan de desarrollo por fases
- Cronograma de implementación
- Microservicios por fase
- Estructura recomendada para cada servicio

**A QUIÉN LE INTERESA:**
- Project managers
- Scrum masters
- Tech leads

**CONSULTA ESTO** para seguimiento del desarrollo.

---

## Guía de Navegación Rápida

### Si quiero... entonces leo...

| Objetivo | Documento |
|----------|-----------|
| Entender qué es el proyecto en 5 minutos | RESUMEN_EJECUTIVO |
| Implementar un nuevo microservicio | ESTRUCTURA_PROYECTO + PROYECTO_PLATAFORMA_CURSOS |
| Entender cómo fluye una request en el sistema | DIAGRAMA_ARQUITECTURA |
| Ver todas las reglas de negocio | PROYECTO_PLATAFORMA_CURSOS |
| Planificar el siguiente sprint | PLAN_DESARROLLO |
| Configurar CI/CD o DevOps | ESTRUCTURA_PROYECTO (ver comandos útiles) |
| Debuggear un problema en auth | DIAGRAMA_ARQUITECTURA (flujo de autenticación) |
| Entender la BD del proyecto | ESTRUCTURA_PROYECTO (sección 6) |

---

## Información Clave de Rápida Referencia

### Microservicios Activos
```
8761  ← Eureka Server (Service Registry)
8080  ← API Gateway (Entry Point)
8081  ← Auth Service (Autenticación + JWT)
```

### Usuario Admin Inicial
```
Email:    admin@cursos.com
Password: admin123
Role:     ROLE_ADMIN
```

### Tecnologías Principales
- **Java:** JDK 21
- **Spring Boot:** 4.0.6 (Eureka/Gateway) y 3.5.14 (Auth)
- **Base Datos:** MySQL 8.0+
- **Autenticación:** JWT (JJWT 0.11.5) + BCrypt
- **Service Discovery:** Eureka Netflix

### URLs Importantes
```
Eureka Dashboard:  http://localhost:8761/eureka/
API Gateway:       http://localhost:8080
Auth Service:      http://localhost:8081
```

---

## Estructura del Repositorio

```
PlataformaCursos/
├── docs/
│   ├── INDICE_DOCUMENTACION.md    (Estás aquí)
│   ├── RESUMEN_EJECUTIVO.txt      (Comienza aquí)
│   ├── ESTRUCTURA_PROYECTO.txt    (Detalles técnicos)
│   ├── DIAGRAMA_ARQUITECTURA.txt  (Visualizaciones)
│   ├── PROYECTO_PLATAFORMA_CURSOS.md (Requisitos)
│   └── PLAN_DESARROLLO.md         (Cronograma)
│
├── ms-01-eureka/           (Eureka Server - COMPLETADO)
├── ms-02-gateway/          (API Gateway - COMPLETADO)
└── ms-03-auth-service/     (Auth Service - COMPLETADO)
    ├── src/main/java/com/plataforma/ms_03_auth_service/
    │   ├── config/         (SecurityConfig, JwtUtil)
    │   ├── controllers/    (AuthController, DTOs)
    │   ├── models/         (User entity)
    │   ├── repositories/   (UserRepository)
    │   └── services/       (UserDetailServiceImpl, DataInitializer)
    └── src/main/resources/
        └── application.properties

Servicios Pendientes: MS-04 a MS-12
```

---

## Cómo Empezar

### Para Nuevos Desarrolladores
1. **Día 1:** Lee RESUMEN_EJECUTIVO.txt (30 min)
2. **Día 2:** Lee DIAGRAMA_ARQUITECTURA.txt (30 min)
3. **Día 2:** Ejecuta los comandos útiles para levantar los servicios (1 hora)
4. **Día 3:** Explora el código de MS-03 (2 horas)

### Para Code Review
1. Consulta ESTRUCTURA_PROYECTO.txt para entender el layout
2. Usa DIAGRAMA_ARQUITECTURA.txt para validar flujos
3. Verifica contra PROYECTO_PLATAFORMA_CURSOS.md para requisitos

### Para Testing
1. Usa los endpoints de ESTRUCTURA_PROYECTO.txt
2. Valida contra DIAGRAMA_ARQUITECTURA.txt para flujos esperados
3. Crea casos de test basándote en PROYECTO_PLATAFORMA_CURSOS.md

---

## Comandos Útiles

```bash
# Compilar todos los servicios
cd /Users/ajordanp/MyProjects/PlataformaCursos
for dir in ms-*; do cd $dir && mvn clean compile && cd ..; done

# Ejecutar Eureka (en terminal 1)
cd ms-01-eureka && mvn spring-boot:run

# Ejecutar Gateway (en terminal 2)
cd ms-02-gateway && mvn spring-boot:run

# Ejecutar Auth Service (en terminal 3)
cd ms-03-auth-service && mvn spring-boot:run

# Probar login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@cursos.com","password":"admin123"}'

# Ver servicios registrados
curl http://localhost:8761/eureka/apps/
```

---

## Versionado de Documentación

| Versión | Fecha | Cambios |
|---------|-------|---------|
| 1.0 | 6 May 2026 | Documentación inicial generada |

---

## FAQ - Preguntas Frecuentes

### P: ¿Dónde veo el diagrama de la arquitectura?
**R:** Abre `DIAGRAMA_ARQUITECTURA.txt`

### P: ¿Cuántos servicios faltan por implementar?
**R:** 9 servicios (MS-04 a MS-12). Ver PLAN_DESARROLLO.md

### P: ¿Cómo hago login?
**R:** POST a `/auth/login` con email y contraseña. Ver comandos útiles.

### P: ¿Dónde está el token JWT?
**R:** Se genera en AuthController usando JwtUtil. Ver ESTRUCTURA_PROYECTO.txt

### P: ¿Qué base de datos se usa?
**R:** MySQL 8.0+, puerto 3306. Actualmente solo db_auth está en uso.

### P: ¿Cómo agregar un nuevo microservicio?
**R:** Lee PROYECTO_PLATAFORMA_CURSOS.md y copia la estructura de MS-03.

---

## Soporte y Contacto

Para dudas sobre la documentación:
- Revisa RESUMEN_EJECUTIVO.txt sección "Próximo Paso"
- Consulta ESTRUCTURA_PROYECTO.txt para detalles técnicos
- Mira DIAGRAMA_ARQUITECTURA.txt para flujos

---

**Última actualización:** 6 de Mayo de 2026
**Estado:** Documentación Completa para Fase 1
**Próxima revisión:** Después de completar MS-04
