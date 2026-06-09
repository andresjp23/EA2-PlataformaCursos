package cursos.ms_07_course_service.controller;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cursos.ms_07_course_service.dto.CourseRequest;
import cursos.ms_07_course_service.dto.CourseResponse;
import cursos.ms_07_course_service.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "Cursos", description = "Gestión de cursos")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Crear curso", description = "Crea un nuevo curso en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del curso a crear", required = true, content = @Content(schema = @Schema(implementation = CourseRequest.class)))
            @Valid @RequestBody CourseRequest request) {
        log.info("POST /courses -> Creando curso: {}", request.getTitle());
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos los cursos activos", description = "Retorna una lista de todos los cursos activos")
    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        log.info("GET /courses -> Obteniendo todos los cursos");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Obtener curso por ID", description = "Busca y retorna un curso por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso encontrado"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @Parameter(description = "ID del curso", required = true) @PathVariable Long id) {
        log.info("GET /courses/{} - Obteniendo curso por ID", id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Obtener cursos por categoría", description = "Retorna todos los cursos asociados a una categoría")
    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(
            @Parameter(description = "ID de la categoría", required = true) @PathVariable Long categoryId) {
        log.info("GET /courses/category/{} - Obteniendo cursos por categoría", categoryId);
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryId));
    }

    @Operation(summary = "Obtener cursos por instructor", description = "Retorna todos los cursos de un instructor")
    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente")
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(
            @Parameter(description = "ID del instructor", required = true) @PathVariable Long instructorId) {
        log.info("GET /courses/instructor/{} - Obteniendo cursos por instructor", instructorId);
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }

    @Operation(summary = "Actualizar curso", description = "Actualiza los datos de un curso existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponse.class))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse> updateCourse(
            @Parameter(description = "ID del curso a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos del curso", required = true, content = @Content(schema = @Schema(implementation = CourseRequest.class)))
            @Valid @RequestBody CourseRequest request) {
        log.info("PUT /courses/{} - Actualizando curso", id);
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @Operation(summary = "Eliminar curso", description = "Realiza un soft delete de un curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Curso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "ID del curso a eliminar", required = true) @PathVariable Long id) {
        log.info("DELETE /courses/{} - Eliminando curso", id);
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar curso", description = "Reactiva un curso eliminado (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Curso reactivado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseResponse.class))),
        @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    @PatchMapping("/{id}/enable")
    public ResponseEntity<CourseResponse> enableCourse(
            @Parameter(description = "ID del curso a reactivar", required = true) @PathVariable Long id) {
        log.info("PATCH /courses/{}/enable - Reactivando curso", id);
        return ResponseEntity.ok(courseService.enableCourse(id));
    }
}