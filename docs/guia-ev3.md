# Guía Paso a Paso — Migración a Maven Multi-Módulo

Proyecto: plataforma de cursos online — 10 microservicios de negocio + Eureka Server + API Gateway (12 módulos en total).

---

## ETAPA 0 — Preparación del entorno

### Paso 0.1 — Verificar herramientas instaladas

```bash
java -version
mvn -version
echo $JAVA_HOME      # Linux/Mac
echo %JAVA_HOME%     # Windows (cmd)
echo $env:JAVA_HOME  # Windows (PowerShell)
```

Confirmar que la versión de Java coincide con la usada en tus microservicios (Java 17 o 21 si usas Spring Boot 3.x). Si `JAVA_HOME` no aparece configurado, configurarlo en variables de entorno antes de seguir.

### Paso 0.2 — Instalar extensiones en VSCode

- Extension Pack for Java
- Spring Boot Extension Pack
- Spring Boot Dashboard
- Maven for Java

---

## ETAPA 1 — Crear la estructura del proyecto padre

### Paso 1.1 — Crear la carpeta raíz

```bash
mkdir proyecto-parent
```

### Paso 1.2 — Mover los microservicios existentes dentro

Mover/copiar dentro de `proyecto-parent/`:

```
proyecto-parent/
├── eureka-server/
├── api-gateway/
├── ms-usuarios/
├── ms-cursos/
├── ms-matriculas/
├── ms-pagos/
├── ms-evaluaciones/
├── ms-certificados/
├── ms-contenido/
├── ms-foros/
├── ms-notificaciones/
└── ms-reportes/
```

(Ajusta los nombres reales de tus 10 microservicios de negocio.)

**No abrir cada microservicio por separado todavía.**

### Paso 1.3 — Eliminar de cada módulo hijo lo que pasará a ser heredado

En cada `pom.xml` hijo (lo harás de forma definitiva en la Etapa 2, pero ya puedes identificarlo):
- La etiqueta `<parent>` apuntará al nuevo `proyecto-parent` en lugar de `spring-boot-starter-parent` directo.
- Las versiones de dependencias que el padre va a gestionar centralizadamente se eliminan del hijo (queda solo `groupId` + `artifactId`, sin `<version>`).

### Paso 1.4 — Crear el `pom.xml` del proyecto padre

Crear `proyecto-parent/pom.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cl.duoc.cursosonline</groupId>
    <artifactId>proyecto-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>proyecto-parent</name>
    <description>Proyecto padre - Plataforma de cursos online</description>

    <!-- Hereda configuración base de Spring Boot -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2024.0.0</spring-cloud.version>
    </properties>

    <!-- Módulos hijos -->
    <modules>
        <module>eureka-server</module>
        <module>api-gateway</module>
        <module>ms-usuarios</module>
        <module>ms-cursos</module>
        <module>ms-matriculas</module>
        <module>ms-pagos</module>
        <module>ms-evaluaciones</module>
        <module>ms-certificados</module>
        <module>ms-contenido</module>
        <module>ms-foros</module>
        <module>ms-notificaciones</module>
        <module>ms-reportes</module>
    </modules>

    <!-- Gestión centralizada de versiones (no fuerza la inclusión, solo fija versión) -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springdoc</groupId>
                <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                <version>2.6.0</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

</project>
```

**Qué hace cada sección:**
- `<parent>` (spring-boot-starter-parent): hereda plugins, encoding, y versiones por defecto de Spring Boot.
- `<modules>`: lista todos los módulos que Maven debe compilar cuando se construya desde la raíz.
- `<dependencyManagement>`: fija versiones (Spring Cloud BOM, Swagger) sin obligar a ningún módulo a incluirlas — cada hijo decide si las usa, pero si las usa, usa esta versión.
- `<pluginManagement>`: evita declarar la versión del plugin de Spring Boot en cada hijo.

### Paso 1.5 — Crear la carpeta `docs/`

```bash
mkdir -p proyecto-parent/docs/diagramas
mkdir -p proyecto-parent/docs/documentacion
touch proyecto-parent/docs/bd-general.sql
touch proyecto-parent/docs/endpoints.md
```

### Paso 1.6 — Abrir el proyecto en VSCode

`Archivo → Abrir Carpeta → proyecto-parent/`

No abrir cada microservicio como carpeta independiente.

### Paso 1.7 — Primera compilación

Desde la raíz `proyecto-parent/`:

```bash
mvn clean install -DskipTests
```

Se omiten los tests intencionalmente: el objetivo es validar estructura, dependencias y compilación. Los tests se incorporan en la Etapa 4.

---

## ETAPA 2 — Configuración de cada microservicio

Revisar en este orden: `eureka-server` → `api-gateway` → cada `ms-xxx` de negocio.

### Paso 2.1 — Ajustar el `<parent>` en cada `pom.xml` hijo

Cada microservicio debe apuntar al nuevo padre en lugar de `spring-boot-starter-parent`:

```xml
<parent>
    <groupId>cl.duoc.cursosonline</groupId>
    <artifactId>proyecto-parent</artifactId>
    <version>1.0.0</version>
    <relativePath>../</relativePath>
</parent>
```

### Paso 2.2 — Eliminar versiones duplicadas

En cada dependencia que ya esté gestionada por el padre o por la BOM de Spring Boot/Cloud (por ejemplo `spring-cloud-starter-netflix-eureka-client`, `spring-boot-starter-web`), eliminar la etiqueta `<version>` y dejar solo `groupId` + `artifactId`.

### Paso 2.3 — Regla de ubicación de dependencias

No mover dependencias específicas al padre.

| Dependencia | Va en |
|---|---|
| `spring-boot-starter-data-jpa` | Servicio con base de datos |
| `mysql-connector-j` | Servicio con base de datos |
| `spring-cloud-starter-openfeign` | Servicio que actúa como cliente Feign |
| `springdoc-openapi-starter-webmvc-ui` | Todos los microservicios de negocio |
| `spring-cloud-starter-netflix-eureka-client` | Todos los servicios registrados (negocio + gateway) |

### Paso 2.4 — Agregar Swagger/OpenAPI a cada microservicio de negocio

En el `pom.xml` de cada `ms-xxx`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

(Sin `<version>`: la toma del `dependencyManagement` del padre.)

Verificar que cada uno expone su documentación en:

```
http://localhost:<puerto-del-servicio>/swagger-ui/index.html
```

### Paso 2.5 — Revisar `application.yml` / `application.properties` de cada módulo

Confirmar en cada microservicio de negocio:

```yaml
server:
  port: 808X   # puerto único, sin colisión con otros módulos

spring:
  application:
    name: ms-xxx   # nombre con el que se registra en Eureka

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

Solo en los servicios con persistencia, agregar también:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/<schema_correspondiente>
    username: root
    password: <password>
  jpa:
    hibernate:
      ddl-auto: update
```

### Paso 2.6 — Confirmar dependencias de testing presentes

Verificar que cada `ms-xxx` de negocio tenga (normalmente ya viene con `spring-boot-starter-test`):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

Esto ya incluye JUnit 5 y Mockito — no es necesario agregarlos por separado.

---

## ETAPA 3 — Ejecución del sistema

### Paso 3.1 — Orden de arranque

1. `eureka-server`
2. Los 10 microservicios de negocio
3. `api-gateway`

### Paso 3.2 — Comandos de ejecución

Desde cada módulo individual:

```bash
mvn spring-boot:run
```

O usando el Spring Boot Dashboard de VSCode, lanzando cada módulo en el orden indicado.

Compilación global desde la raíz (no levanta los servicios, solo compila):

```bash
mvn clean install -DskipTests
```

### Paso 3.3 — Checklist de validación

- [ ] `eureka-server` levantado en `http://localhost:8761` muestra los 10 microservicios + gateway registrados.
- [ ] Cada microservicio expone su Swagger en `http://localhost:<puerto>/swagger-ui/index.html`.
- [ ] El `api-gateway` enruta correctamente al menos una petición de prueba hacia un microservicio de negocio.
- [ ] No hay errores de "Application failed to start" por puertos duplicados o beans en conflicto.

---

## ETAPA 4 — Calidad y pruebas unitarias

Iniciar esta etapa solo cuando la Etapa 3 esté validada y el sistema completo funcione.

### Paso 4.1 — Confirmar dependencias de testing

Ya verificadas en el Paso 2.6 (`spring-boot-starter-test` con JUnit 5 + Mockito).

### Paso 4.2 — Crear test de Service (capa de negocio)

Ejemplo de esqueleto, por cada `ms-xxx`:

```java
@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    @Test
    void deberiaRetornarCursoPorId() {
        Curso curso = new Curso(1L, "Spring Boot Avanzado");
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Curso resultado = cursoService.obtenerPorId(1L);

        assertEquals("Spring Boot Avanzado", resultado.getNombre());
    }
}
```

### Paso 4.3 — Crear test de Controller

```java
@WebMvcTest(CursoController.class)
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursoService;

    @Test
    void deberiaRetornar200AlConsultarCurso() throws Exception {
        when(cursoService.obtenerPorId(1L)).thenReturn(new Curso(1L, "Spring Boot Avanzado"));

        mockMvc.perform(get("/cursos/1"))
                .andExpect(status().isOk());
    }
}
```

### Paso 4.4 — Crear test de Repository (solo módulos con persistencia)

```java
@DataJpaTest
class CursoRepositoryTest {

    @Autowired
    private CursoRepository cursoRepository;

    @Test
    void deberiaGuardarYRecuperarCurso() {
        Curso curso = new Curso(null, "Spring Boot Avanzado");
        Curso guardado = cursoRepository.save(curso);

        assertNotNull(guardado.getId());
    }
}
```

Repetir el patrón de los pasos 4.2–4.4 adaptado a las entidades reales de cada uno de los 10 microservicios de negocio.

### Paso 4.5 — Compilación final, ya sin omitir pruebas

```bash
mvn clean install
```

(sin `-DskipTests`)

---

## Resumen del orden de ejecución completo

1. **Etapa 0** — Verificar Java, JAVA_HOME y Maven.
2. **Etapa 1** — Crear `proyecto-parent/`, mover módulos, crear `pom.xml` padre y `docs/`, primera compilación con `-DskipTests`.
3. **Etapa 2** — Ajustar `<parent>` y versiones en cada hijo, agregar Swagger, ordenar JPA/MySQL/OpenFeign/Eureka según corresponda, revisar `application.yml`.
4. **Etapa 3** — Levantar Eureka → microservicios → Gateway, validar registro y comunicación.
5. **Etapa 4** — Agregar tests de Service/Controller/Repository, compilar sin `-DskipTests`.