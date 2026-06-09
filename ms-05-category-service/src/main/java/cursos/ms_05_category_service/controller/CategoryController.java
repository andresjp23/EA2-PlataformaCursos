package cursos.ms_05_category_service.controller;

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

import cursos.ms_05_category_service.dto.CategoryRequest;
import cursos.ms_05_category_service.dto.CategoryResponse;
import cursos.ms_05_category_service.service.CategoryService;
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
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de cursos")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la categoría a crear", required = true, content = @Content(schema = @Schema(implementation = CategoryRequest.class)))
            @Valid @RequestBody CategoryRequest request){
        log.info("POST /categories -> Creando categoria: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener todas las categorías activas", description = "Retorna una lista de todas las categorías activas")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        log.info("GET /categories -> Obteniendo todas las cagtegorias.");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(summary = "Obtener todas las categorías (admin)", description = "Retorna todas las categorías incluyendo las inactivas")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente")
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesIncludingInactive() {
        log.info("GET /categories/all - Obteniendo todas las categorías (admin)");
        return ResponseEntity.ok(categoryService.getAllCategoriesIncludingInactive());
    }

    @Operation(summary = "Obtener categoría por ID", description = "Busca y retorna una categoría por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "ID de la categoría", required = true) @PathVariable Long id) {
        log.info("GET /categories/{} - Obteniendo categoría por ID", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "ID de la categoría a actualizar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos de la categoría", required = true, content = @Content(schema = @Schema(implementation = CategoryRequest.class)))
            @Valid @RequestBody CategoryRequest request) {
        log.info("PUT /categories/{} - Actualizando categoría", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Eliminar categoría", description = "Realiza un soft delete de una categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID de la categoría a eliminar", required = true) @PathVariable Long id) {
        log.info("DELETE /categories/{} - Eliminando categoría", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar categoría", description = "Reactiva una categoría eliminada (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoría reactivada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PatchMapping("/{id}/enable")
    public ResponseEntity<CategoryResponse> enableCategory(
            @Parameter(description = "ID de la categoría a reactivar", required = true) @PathVariable Long id) {
        log.info("PATCH /categories/{}/enable - Reactivando categoría", id);
        return ResponseEntity.ok(categoryService.enableCategory(id));
    }

}