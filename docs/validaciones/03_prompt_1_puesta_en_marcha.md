# 🧩 PROMPT 1 — Puesta en Marcha (Recomendado)

> **Cuándo usarlo:** Al inicio del proyecto, cuando ya tienes los microservicios esbozados pero antes de escribir código.
> Este prompt es CLAVE para evitar errores de arquitectura que son difíciles de corregir después.

---

## Cómo Usarlo en Claude / Copilot / ChatGPT

1. Copia el bloque de texto de abajo.
2. Pégalo en el chat de tu IA preferida.
3. **Agrega debajo** la descripción de tu sistema: nombre del proyecto, lista de microservicios que tienes en mente, contexto del dominio.

---

## Prompt

```
Actúa como un experto en arquitectura de microservicios con Spring Boot y evaluación académica basada en rúbricas.

Tengo un proyecto de microservicios ya iniciado por estudiantes. Tu tarea es analizar, ordenar y corregir el enfoque antes de continuar con el desarrollo.

**Objetivo**
Asegurar que el proyecto esté correctamente estructurado para cumplir con la Evaluación 2.

**Instrucciones**
- Identifica y lista todos los microservicios del sistema
- Detecta:
  - Microservicios mal definidos
  - Responsabilidades duplicadas
  - Dependencias incorrectas
- Propón una versión CORREGIDA del sistema

**Entregables esperados**

1. Lista final de microservicios
   - Nombre correcto (sin tildes ni ñ)
   - Responsabilidad clara

2. Orden de desarrollo recomendado
   - Justificado por dependencias reales

3. Mapa de comunicación
   - Quién consume a quién (Feign/WebClient)

4. Problemas detectados (CRÍTICO)
   - Qué está mal actualmente
   - Por qué afecta la evaluación
   - Cómo corregirlo

5. Ajustes obligatorios para cumplir rúbrica 2
   - Separación de responsabilidades
   - Uso correcto de bases de datos
   - Preparación para API Gateway y Eureka

**Restricción**
NO generar código aún.
Solo análisis, corrección y planificación técnica.

**Inicio**
Analiza el sistema y entrégame la versión optimizada.
```

---

## Qué Agregar Después del Prompt

Después de pegar el prompt, añade algo como esto (adaptado a tu proyecto):

```
Mi proyecto es: [nombre del sistema, ej: Plataforma de cursos online]

Microservicios que tengo planificados hasta ahora:
1. [nombre] - [descripción breve]
2. [nombre] - [descripción breve]
...

Contexto del dominio:
[2-3 oraciones describiendo para qué sirve el sistema y quiénes lo usan]
```

---

## Qué Esperar como Respuesta

La IA debería entregarte:

| Entregable | Descripción |
|------------|-------------|
| Lista corregida | Microservicios con nombres en inglés/sin tildes y responsabilidad única |
| Orden de desarrollo | Desde los más independientes (sin dependencias) hacia los que consumen a otros |
| Mapa de comunicación | Diagrama textual de quién llama a quién vía Feign |
| Problemas detectados | Lista de errores de diseño y cómo corregirlos |
| Ajustes para la rúbrica | Cambios necesarios para cumplir con los indicadores de evaluación |

---

## Señales de que el Prompt Funcionó Bien

- ✅ La IA detectó microservicios con responsabilidades mezcladas y los separó.
- ✅ Propuso un orden que empieza por servicios sin dependencias externas.
- ✅ Identificó si algún microservicio compartía BD con otro (lo cual está prohibido).
- ✅ Mencionó explícitamente el API Gateway y Eureka en el esquema.
- ✅ El mapa de comunicación refleja flujos de negocio reales de tu dominio.

---

## Próximo Paso

Una vez validada la arquitectura con este prompt, continúa con el **PROMPT 2 — Desarrollo**.
