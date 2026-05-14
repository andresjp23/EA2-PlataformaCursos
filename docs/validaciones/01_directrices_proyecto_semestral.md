# 📋 Directrices Mínimas — Proyecto Semestral
**Asignatura:** Desarrollo Fullstack I | **Docente:** Mauricio González V.

---

## Contexto General

El proyecto consiste en diseñar e implementar un **sistema basado en arquitectura de microservicios**.

| Ítem | Detalle |
|------|---------|
| Equipos | Mínimo 2 — Máximo 3 integrantes |
| Evaluación 2 | Proyecto funcional + Defensa oral |
| Evaluación 3 | Proyecto optimizado (Pruebas, Documentación, Despliegue) + Defensa oral |
| Complejidad mínima | Problema real, múltiples módulos, lógica de negocio |

> ⚠️ Los requisitos aquí listados son el **mínimo exigido**. Se puede (y se recomienda) ir más allá.

---

## Requisitos del Tema

El tema elegido debe:

- Representar un **problema real** o necesidad concreta de un sistema de información.
- Ser un **sistema completo de gestión o servicio digital** (no proyectos simples o meramente académicos).
- Tener **complejidad acorde a tercer semestre** (fundamentos de programación y BD ya dominados).
- Incluir **múltiples módulos funcionales y componentes backend** que justifiquen el uso de microservicios.

---

## Arquitectura del Sistema

- Framework obligatorio: **Spring Boot**
- Mínimo: **10 microservicios independientes**

Cada microservicio debe:
- Tener una **responsabilidad clara** dentro del sistema.
- Manejar su propia **lógica de negocio**.
- Exponer una **API REST** para su consumo.
- Contar con sus propios **endpoints funcionales**.
- Estar **completamente desacoplado** de los demás.

---

## Comunicación entre Microservicios

El sistema debe demostrar **interacción real** entre servicios, incluyendo:

- Consulta de datos entre servicios.
- Validación de información entre microservicios.
- Flujo de datos entre distintos módulos.
- Comunicación mediante **WebClient** o **Feign Client**.

Elementos obligatorios de infraestructura:
- **API Gateway** (punto de entrada único)
- **Service Registry** como **Eureka** (o equivalente)

---

## Persistencia de Datos

- Cada microservicio debe tener su **propia base de datos independiente**.
- **No se permite** compartir tablas entre microservicios.
- No duplicar estructuras de datos entre servicios.
- Bases de datos **correctamente normalizadas**.
- Debe existir un **modelo relacional validado** con diagramas (DER).

**Motores permitidos:**
- ✅ MySQL (recomendado)
- ✅ Oracle

**Entornos sugeridos:**
- XAMPP
- Laragon

El proyecto debe evidenciar:
- Entidades correctamente modeladas.
- Relaciones entre entidades.
- Integridad de los datos.
- Validaciones y reglas de negocio a nivel de aplicación.

---

## Roles de Usuario

El sistema debe contemplar **entre 2 y 3 roles de usuario diferenciados**.

Cada rol debe:
- Tener funcionalidades distintas dentro del sistema.
- Manejar permisos y privilegios diferentes.
- Interactuar con distintos módulos del sistema.

Ejemplos de roles: `Administrador`, `Usuario cliente`, `Operador del sistema`.

---

## Funcionalidades Mínimas del Sistema

- Múltiples operaciones **CRUD**.
- Operaciones **personalizadas** adicionales a los CRUD básicos.
- **Validaciones de datos**.
- **Reglas de negocio**.
- Consultas a bases de datos.
- **Flujo de información entre microservicios**.
- Las APIs deben permitir **gestionar completamente** los procesos del sistema.

---

## Seguridad Básica

- Encriptación de contraseñas (**BCrypt**) antes de almacenarlas.
- Autenticación de usuarios.
- Generación de **tokens de sesión** en el proceso de login (**JWT**).
- Validación de accesos según roles de usuario (**RBAC**).

---

## Control de Versiones

- Repositorio obligatorio: **GitHub** (público, con acceso al docente).
- El repositorio debe evidenciar:
  - Avance progresivo del desarrollo.
  - Participación de **todos los integrantes** del equipo.
  - Historial de cambios claro.
- Buenas prácticas: ramas de desarrollo, commits frecuentes, estructura organizada.

---

## Documentación, Pruebas y Despliegue *(Evaluación 3)*

- Documentación técnica del sistema.
- Pruebas unitarias.
- Despliegue de los microservicios (local, ngrok/localtunnel, o servicios cloud gratuitos como Railway/Render/Koyeb).
- El sistema debe quedar disponible en una **URL pública** para consumir las APIs.

---

## Proyectos Aceptables ✅

- Sistema de gestión de reservas de hoteles
- Plataforma de gestión de pedidos para restaurantes
- Sistema de gestión de bibliotecas
- Plataforma de gestión de ventas online
- Sistema de gestión de inventario con logística
- Marketplace de servicios o productos
- Plataforma de gestión de reservas de servicios

## Proyectos NO Aceptables ❌

- Listas básicas de tareas
- Agendas simples
- CRUD simples sin lógica de negocio
- Sistemas sin interacción entre microservicios
- Aplicaciones con una única base de datos compartida

---

## Herramientas y Metodologías Sugeridas

### I. Arquitectura
| Herramienta | Uso |
|-------------|-----|
| Spring Boot | Framework principal para crear microservicios |
| Microservicios desacoplados | Cada módulo es independiente con su propia BD |

### II. Base de Datos
| Herramienta | Uso |
|-------------|-----|
| Base de Datos por Servicio | Patrón obligatorio — BD propia por microservicio |
| Spring Data JPA | Interacción Java ↔ BD sin SQL manual complejo |
| Diagrama Entidad-Relación (DER) | Modelado previo a la creación de tablas |

### III. Documentación
| Herramienta | Uso |
|-------------|-----|
| SpringDoc OpenAPI / Swagger | Página interactiva para visualizar y probar APIs |
| Anotaciones Swagger (`@Operation`) | Descripciones amigables en la documentación |
| Markdown (README.md) | Guía de instalación y ejecución del proyecto |

### IV. Pruebas
| Herramienta | Uso |
|-------------|-----|
| JUnit 5 | Framework para pruebas automáticas |
| Mockito | Simular objetos complejos en pruebas aisladas |
| Spring Boot Validation (`@NotNull`, etc.) | Validar datos antes de procesarlos |

### V. Comunicación
| Herramienta | Uso |
|-------------|-----|
| Feign Client | Comunicación sincrónica entre microservicios |
| Apache Kafka | Comunicación asincrónica por eventos |
| Eureka (Service Discovery) | Registro dinámico de microservicios |
| API Gateway | Punto de entrada único para solicitudes externas |

### VI. Seguridad
| Herramienta | Uso |
|-------------|-----|
| Spring Security | Autenticación y autorización |
| BCrypt | Cifrado de contraseñas |
| JWT (JJWT) | Tokens de sesión seguros y sin estado |
| RBAC | Control de acceso por roles |

### VII. Despliegue
| Herramienta | Uso |
|-------------|-----|
| Maven / Gradle | Gestión de dependencias y empaquetado `.jar` |
| Docker / Docker Compose | Contenedores para ejecución reproducible |
| Ngrok / Localtunnel | URL pública temporal para pruebas externas |
| Railway / Render / Koyeb | Cloud gratuito para despliegue en producción |

### VIII. Gestión de Código
| Herramienta | Uso |
|-------------|-----|
| GitHub | Control de versiones obligatorio |
| Branches / Commits | Organización del historial de desarrollo |
