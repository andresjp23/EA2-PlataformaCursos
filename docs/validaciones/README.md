# 📁 Guía del Proyecto — DSY1103 Desarrollo Fullstack I

> Esta carpeta contiene toda la documentación necesaria para desarrollar y revisar el proyecto semestral de arquitectura de microservicios.

---

## Estructura de Archivos

```
📁 proyecto-docs/
├── README.md                          ← Este archivo (flujo general)
├── 01_directrices_proyecto_semestral.md  ← Requisitos del docente
├── 02_evaluacion_parcial_2.md            ← Rúbrica completa de la Eval 2 (45%)
├── 03_prompt_1_puesta_en_marcha.md       ← Prompt para validar arquitectura
└── 04_prompt_2_desarrollo.md             ← Prompt para desarrollar microservicio por microservicio
```

---

## Flujo Recomendado de Trabajo

### Fase 1 — Planificación (antes de codear)

```
1. Lee 01_directrices_proyecto_semestral.md
   → Confirma que tu tema cumple los requisitos mínimos

2. Lee 02_evaluacion_parcial_2.md
   → Revisa la rúbrica completa para saber exactamente qué se evalúa

3. Usa el PROMPT 1 (03_prompt_1_puesta_en_marcha.md)
   → Pégalo en Claude/ChatGPT con la descripción de tu sistema
   → Obtén: lista de microservicios corregida, orden de desarrollo, mapa de comunicación
   → NO avances a codear hasta validar la arquitectura
```

### Fase 2 — Desarrollo (microservicio por microservicio)

```
4. Usa el PROMPT 2 (04_prompt_2_desarrollo.md)
   → Se desarrolla de a UN microservicio por vez
   → Cada microservicio debe pasar el checklist antes de continuar
   → Prueba en Postman antes de avanzar al siguiente

5. Repite el ciclo hasta completar los 10+ microservicios
```

### Fase 3 — Integración

```
6. Configura Eureka Server (Service Registry)
7. Configura API Gateway
8. Implementa comunicación Feign/WebClient entre microservicios
9. Prueba flujos completos end-to-end en Postman
```

### Fase 4 — Cierre antes de la entrega

```
10. Revisa la rúbrica ítem por ítem (02_evaluacion_parcial_2.md)
11. Asegura que cada integrante tenga commits en GitHub
12. Redacta el README.md del proyecto con:
    - Descripción del sistema
    - Nombres del equipo
    - Funcionalidades implementadas
    - Pasos para ejecutar
13. Sube a AVA grupal e individual ANTES de la semana 10
14. NO hagas commits después de la fecha de entrega
```

---

## Cómo Usar Estos Archivos en Open Code (VS Code)

1. Coloca esta carpeta en la raíz de tu workspace de VS Code.
2. Abre cualquier `.md` con `Ctrl+Shift+V` para verlo renderizado.
3. Instala la extensión **"Markdown All in One"** para mejor experiencia.
4. Usa el **panel de explorador** a la izquierda para navegar entre archivos mientras codeas.

### Extensiones recomendadas para VS Code

| Extensión | Para qué sirve |
|-----------|---------------|
| Markdown All in One | Renderizar y editar markdown cómodamente |
| Spring Boot Extension Pack | Soporte completo para Spring Boot |
| Java Extension Pack | IntelliSense, debug y refactor para Java |
| REST Client | Probar endpoints directo desde VS Code (alternativa a Postman) |
| GitLens | Ver historial de commits y blame en el código |
| Checkboxes | Marcar los checklist de los markdowns interactivamente |

---

## Comandos Útiles para el Proyecto

```bash
# Crear nuevo proyecto Spring Boot (desde terminal)
# Recomendado: usar https://start.spring.io/

# Compilar y ejecutar un microservicio
./mvnw spring-boot:run

# Compilar sin ejecutar
./mvnw clean package -DskipTests

# Ver logs en tiempo real
tail -f logs/application.log
```

---

## Recordatorios Críticos

> 🔴 **Congelar el repo antes de la defensa.** Cualquier commit después de la entrega = nota 1.0 automática.

> 🔴 **La defensa es individual.** Aunque el proyecto sea grupal, cada uno debe saber todo el código.

> 🟡 **Los 3 indicadores de mayor peso en la defensa son:**
> - IE 2.3.4 → Cambios en logs/excepciones/HTTP en vivo (13%)
> - IE 2.2.5 → Cambios en validaciones/reglas en vivo (12%)
> - IE 2.4.4 → Cambios en comunicación entre microservicios (12%)
>
> Practica hacer modificaciones en vivo y probarlas en Postman.

> 🟢 **Prepara ejemplos de cada cosa:** una validación que falla, un log que se activa, un error HTTP 404 vs 400.
