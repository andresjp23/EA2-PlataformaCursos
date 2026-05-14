# 🚀 PROMPT 2 — Desarrollo para Evaluación 2

> **Cuándo usarlo:** Después de validar la arquitectura con el Prompt 1.
> Se usa **una vez por microservicio**, en orden, avanzando de a uno.

---

## Flujo de Trabajo Recomendado

```
Prompt 1 (arquitectura validada)
        ↓
Prompt 2 → Microservicio 1
        ↓ (cuando esté listo y probado)
Prompt 2 → Microservicio 2
        ↓
      ... (repetir hasta completar los 10+)
```

---

## Cómo Usarlo

1. Copia el prompt de abajo.
2. Pégalo en tu IA con el contexto de tu proyecto ya establecido (o recuérdale el dominio).
3. La IA desarrollará **solo el primer microservicio** y se detendrá.
4. Tú pruebas en Postman, revisas que funcione, y luego dices: `"continuar con el siguiente microservicio"`.

---

## Prompt

```
Actúa como un experto en desarrollo backend con Spring Boot y evaluación académica basada en rúbricas.

Ya existe una arquitectura validada de microservicios.
Tu tarea es guiar el desarrollo para cumplir con TODOS los criterios de la Evaluación 2.

**Objetivo**
Desarrollar microservicios completamente funcionales, evaluables y alineados con la rúbrica.

**Paso 1**
Lista los microservicios en orden de desarrollo.

**Paso 2 (CRÍTICO)**
Desarrolla SOLO el primer microservicio.

Debe incluir:
- Estructura completa (CSR)
- Entidad(es) con anotaciones JPA
- Script SQL de creación de BD
- Repository (JpaRepository)
- Service (interfaz + implementación)
- Controller (CRUD completo)
- DTOs separados de las entidades
- Validaciones con Bean Validation (@NotNull, @Size, etc.)
- @ControllerAdvice para manejo centralizado de errores
- Logs obligatorios con @Slf4j en todas las capas
- application.yml / application.properties completo

**Paso 3**
Entrega:
- Checklist de cumplimiento de rúbrica
- Cómo probar cada endpoint en Postman

**Regla obligatoria**
DETENERSE después del primer microservicio.
Esperar instrucción: "continuar con el siguiente microservicio"

**Evaluación interna (muy importante)**
Antes de entregar, valida internamente:
- ¿Cumple patrón CSR?
- ¿Tiene validaciones en DTOs?
- ¿Tiene logs en todas las capas (controller, service)?
- ¿CRUD completo y funcional?
- ¿Listo para integrar con Feign desde otro microservicio?

Si algo falta, corrígelo antes de entregar.

**Inicio**
Comienza con el Paso 1.
```

---

## Qué Debe Incluir Cada Microservicio Generado

### Estructura de Paquetes Esperada
```
com.tuapp.nombreservicio/
├── controller/
│   └── NombreController.java
├── service/
│   ├── NombreService.java          (interfaz)
│   └── NombreServiceImpl.java      (implementación)
├── repository/
│   └── NombreRepository.java
├── model/
│   └── NombreEntity.java
├── dto/
│   ├── NombreRequestDTO.java
│   └── NombreResponseDTO.java
├── exception/
│   └── GlobalExceptionHandler.java (@ControllerAdvice)
└── NombreApplication.java
```

### Checklist por Microservicio

Antes de avanzar al siguiente, verifica:

- [ ] Patrón CSR implementado correctamente (sin lógica en el controller)
- [ ] Entidad con anotaciones JPA completas
- [ ] Script SQL funcional y consistente con la entidad
- [ ] Repository extiende `JpaRepository`
- [ ] Service tiene interfaz + implementación separada
- [ ] DTOs separados de las entidades (Request y Response)
- [ ] Validaciones en DTO con Bean Validation
- [ ] `@ControllerAdvice` captura excepciones y retorna JSON estructurado
- [ ] `@Slf4j` con logs en controller y service (al menos en cada método)
- [ ] Todos los endpoints retornan `ResponseEntity<>`
- [ ] `application.properties` configurado con datasource correcto
- [ ] Probado en Postman: GET, POST, PUT, DELETE
- [ ] Listo para ser consumido por otro microservicio vía Feign

---

## Instrucción para Avanzar al Siguiente

Una vez probado y verificado el microservicio actual, di exactamente:

```
continuar con el siguiente microservicio
```

La IA aplicará el mismo proceso al siguiente en el orden definido.

---

## Notas para la Defensa

> Recuerda que en la defensa técnica te pueden pedir:
> - Agregar una validación nueva en tiempo real → debes saber dónde va (DTO) y qué anotación usar.
> - Cambiar un código HTTP de respuesta → debes saber qué devuelve `ResponseEntity.ok()`, `ResponseEntity.status(404)`, etc.
> - Explicar un log → debes saber en qué capa está y qué evento registra.
> - Modificar la comunicación Feign → debes entender la interfaz y qué endpoint consume.
>
> **Aprende el código, no solo lo copies.**
