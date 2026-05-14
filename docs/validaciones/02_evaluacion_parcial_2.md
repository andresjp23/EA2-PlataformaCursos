# 📝 Evaluación Parcial 2 — Encargo con Defensa Técnica
**Asignatura:** DSY1103 — Desarrollo Fullstack I
**Tiempo asignado:** 2 semanas / 15 min defensa
**Ponderación:** **45% de la nota final**

---

## Descripción General

Esta evaluación corresponde al **primer avance técnico formal** del Proyecto Semestral de Arquitectura de Microservicios.

- Equipos de **2 a 3 integrantes**.
- Mínimo **10 microservicios** implementados por equipo (sin máximo, siempre que haya coherencia y separación funcional).
- El encargo se asigna en **semana 1** y se entrega en **semana 10** (antes de la primera sesión de evaluación).
- La defensa técnica se realiza en **semanas 11 y 12**, máximo **15 minutos por estudiante**, orden al azar.

> ⚠️ **La defensa es individual.** La nota depende exclusivamente de lo que cada uno demuestre, independientemente de cómo esté el proyecto grupal.

> 🚨 Si un estudiante no puede explicar el proyecto, ejecutarlo o modificar el código en vivo, se asumirá que **no participó activamente en el desarrollo**.

---

## Propósito de la Evaluación

Verificar la capacidad del equipo para diseñar, construir y justificar una arquitectura distribuida de microservicios con:

- Persistencia real, reglas de negocio claras, validaciones robustas y manejo de errores.
- Dominio del patrón **CSR** (Controller–Service–Repository).
- Manipulación de base de datos con JPA.
- Desarrollo de endpoints REST completos y funcionales.
- Buenas prácticas de organización y calidad de código.
- Herramientas de trabajo colaborativo.

---

## Requisitos Obligatorios

### 1. Persistencia Real con JPA + Hibernate
- Entidades con `@Entity`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`, etc.
- Repositorios con `JpaRepository` para CRUD reales.
- Configuración de datasource y dialecto en `application.properties`.
- Scripts SQL o migraciones con Flyway/Liquibase.

### 2. Modelado de Datos y Normalización
- Modelos relacionales coherentes con el dominio del proyecto.
- Relaciones correctas entre tablas y entidades.
- Claves primarias, foráneas e integridad referencial.

### 3. Validaciones con Bean Validation (JSR 380)
- Anotaciones de validación en DTOs y entidades (`@NotNull`, `@Size`, etc.).
- Validación en controladores con respuestas consistentes ante entradas inválidas.
- Separación entre **DTOs y entidades** para validar datos de forma limpia y segura.

### 4. Manejo de Excepciones
- `try/catch` en la capa de servicio cuando corresponda.
- `ResponseEntity` con códigos HTTP adecuados en todos los endpoints.
- `@ControllerAdvice` para manejo **centralizado** de errores.

### 5. Patrón CSR
```
Controller  →  Manejo de solicitudes REST
Service     →  Lógica de negocio
Repository  →  Acceso a datos
```
- Sin mezclar responsabilidades entre capas.
- DTOs para comunicación entre capas cuando sea apropiado.

### 6. Respuestas REST Correctas y Consistentes
- Todos los endpoints retornan **JSON estructurado**.
- Rutas semánticas, métodos HTTP adecuados (GET, POST, PUT, DELETE).
- Uso obligatorio de **`ResponseEntity`** para controlar respuestas.

### 7. Logs con SLF4J (`@Slf4j`)
- Mensajes claros que permitan trazabilidad entre capas.
- Logs en eventos relevantes: creación, actualización, errores, validaciones fallidas.

### 8. Comunicación entre Microservicios
- Consumo de endpoints entre microservicios mediante **WebClient** o **Feign Client**.
- Manejo de timeouts, errores y validación de datos recibidos.
- Pruebas de integración mínimas con **Postman** u otra herramienta REST.

### 9. Buenas Prácticas y Calidad de Código
- Código comentado cuando sea necesario.
- Nombres de clases, métodos y variables significativos.
- Estructura limpia y modular.
- Uso adecuado de paquetes según responsabilidad.
- Eliminación de código muerto o duplicado antes de la entrega.

### 10. Control de Versiones y Colaboración
- Repositorio **GitHub público** con acceso al docente y compañeros.
- Commits técnicos, descriptivos y frecuentes.
- Tablero de tareas en **Trello** u otra herramienta colaborativa.

---

## Forma de Entrega

- ✅ Repositorio GitHub público con:
  - Carpeta del proyecto completa.
  - `README.md` con: descripción del proyecto, nombres del equipo, funcionalidades implementadas, pasos para ejecutar.
  - Commits técnicos y descriptivos (sin textos no técnicos).
- ✅ Código fuente subido al **enlace AVA grupal** por un solo integrante del equipo.
- ✅ **Enlace AVA individual** activado con: nombre, apellido, número de equipo y nombre de la aplicación.

### ⛔ Penalizaciones Automáticas
| Situación | Consecuencia |
|-----------|--------------|
| Cambios en el repositorio después de la entrega (antes de la defensa) | Nota 1.0 automática (grupal e individual) |
| No subir código a AVA grupal | Nota 1.0 automática |
| No activar enlace AVA individual | Nota 1.0 individual (sin derecho a defensa) |
| Recurso en el código no implementado en la app | Ítem calificado con 0 inmediatamente |

---

## Consideraciones para la Defensa

- No se permite uso de **IA, herramientas automáticas de código o asistencia externa** durante la sesión.
- Internet limitado estrictamente a descarga de dependencias Spring Boot.
- El docente monitoreará continuamente el trabajo.
- Cualquier indicio de **plagio o colaboración no autorizada** implica anulación inmediata.
- Puedes pedir que repitan la pregunta si no la entiendes.
- Puedes anotar los pasos antes de ejecutar.

---

## Rúbrica de Evaluación

### Escala de Desempeño

| Nivel | % Logro | Descripción |
|-------|---------|-------------|
| Muy buen desempeño | 100% | Logra todos los aspectos evaluados del indicador |
| Desempeño aceptable | 60% | Logra los elementos básicos, pero con omisiones o errores menores |
| Desempeño incipiente | 30% | Importantes omisiones o errores; no se puede considerar competente |
| Desempeño no logrado | 0% | Ausencia o desempeño incorrecto |

---

### DIMENSIÓN GRUPAL — Entrega de Encargo (30%)

| ID | Indicador | Ponderación |
|----|-----------|:-----------:|
| IE 1.2.1 | Estructura el microservicio aplicando el patrón CSR con separación real de responsabilidades y paquetes por capa | 3% |
| IE 2.1.1 | Modela esquemas de BD normalizados y coherentes con el dominio, separados por microservicio, con entidades JPA, relaciones y atributos según diseño relacional | 2% |
| IE 2.1.2 | Integra operaciones CRUD completas conectando JpaRepository con endpoints REST funcionales y retornos JSON coherentes | 3% |
| IE 2.2.1 | Implementa las reglas de negocio del dominio, garantizando restricciones, flujos y requisitos funcionales completos | 4% |
| IE 2.2.2 | Implementa validaciones con Bean Validation y relaciones entre entidades que aseguran integridad de datos | 3% |
| IE 2.2.3 | Integra relaciones entre entidades manteniendo coherencia e integridad referencial | 2% |
| IE 2.3.1 | Implementa manejo de excepciones en todos los endpoints con respuestas coherentes y uniformes | 3% |
| IE 2.3.2 | Integra logs estructurados en puntos clave del flujo del microservicio según buenas prácticas | 2% |
| IE 2.4.1 | Implementa comunicaciones entre microservicios (WebClient/Feign), asegurando interoperabilidad en los flujos que requieren info remota | 3% |
| IE 2.4.2 | Configura endpoints REST que exponen o consumen datos remotos respetando convenciones REST | 2% |
| IE 2.5.1 | Gestiona repositorio GitHub ordenado con commits técnicos, progresivos y distribuidos equitativamente | 2% |
| IE 2.5.2 | Organiza tareas en Trello u otra herramienta colaborativa evidenciando roles y avance | 1% |
| **TOTAL GRUPAL** | | **30%** |

---

### DIMENSIÓN INDIVIDUAL — Defensa Técnica (70%)

| ID | Indicador | Ponderación |
|----|-----------|:-----------:|
| IE 2.1.3 | Justifica decisiones de modelado, relaciones, normalización y consistencia con el dominio | 4% |
| IE 2.1.4 | Realiza modificaciones en vivo (entidades, relaciones, CRUD) asegurando compilación y persistencia correcta | **6%** |
| IE 2.2.4 | Justifica la lógica de negocio en la capa de servicio, demostrando comprensión del flujo interno del microservicio | **6%** |
| IE 2.2.5 | Realiza cambios en el código (añadir validaciones o reglas) y verifica su efecto mediante pruebas REST | **12%** |
| IE 2.3.3 | Interpreta logs, excepciones y errores del microservicio, explicando el flujo y trazabilidad entre capas | **7%** |
| IE 2.3.4 | Realiza cambios en el código demostrando control de logs, excepciones y códigos HTTP | **13%** |
| IE 2.4.3 | Explica el flujo de comunicación entre microservicios, justificando el manejo y su impacto en el negocio | 5% |
| IE 2.4.4 | Realiza cambios en la comunicación entre microservicios demostrando correcta obtención y uso de info remota | **12%** |
| IE 2.5.3 | Explica su aporte personal al proyecto, justificando commits, tareas asignadas y participación individual | 5% |
| **TOTAL INDIVIDUAL** | | **70%** |

> 💡 Los indicadores en **negrita** son los de mayor peso en la defensa. Prioriza dominarlos.

---

## Checklist Rápido de Preparación para la Defensa

- [ ] Puedo explicar cada microservicio y su responsabilidad
- [ ] Puedo navegar el código en vivo sin ayuda
- [ ] Sé agregar una validación nueva en tiempo real y probarla en Postman
- [ ] Sé agregar/modificar un log y explicar qué traza
- [ ] Sé modificar un código HTTP de respuesta y justificarlo
- [ ] Sé explicar cómo funciona la comunicación Feign/WebClient entre dos servicios específicos
- [ ] Puedo señalar mis commits en GitHub y explicar qué hice en cada uno
- [ ] Conozco las reglas de negocio de cada microservicio
