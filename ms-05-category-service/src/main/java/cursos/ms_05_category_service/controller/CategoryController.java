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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // POST /categories -> crear una nueva categoria
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request){
        log.info("POST /categories -> Creando categoria: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        // HttpStatus.CREATED = 201 (recurso creado exitosamente)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /categories -> Obtener todas las categorias activas
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){
        log.info("GET /categories -> Obteniendo todas las cagtegorias.");
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // GET /categories/all - Obtener todas las categorías (incluyendo inactivas)
    // Ruta específica ANTES de /{id} para que no se interprete como ID
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesIncludingInactive() {
        log.info("GET /categories/all - Obteniendo todas las categorías (admin)");
        return ResponseEntity.ok(categoryService.getAllCategoriesIncludingInactive());
    }
    // GET /categories/{id} - Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("GET /categories/{} - Obteniendo categoría por ID", id);
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
    // PUT /categories/{id} - Actualizar una categoría
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.info("PUT /categories/{} - Actualizando categoría", id);
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
    // DELETE /categories/{id} - Eliminar (soft delete) una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /categories/{} - Eliminando categoría", id);
        categoryService.deleteCategory(id);
        // HttpStatus.NO_CONTENT = 204 (sin contenido en la respuesta)
        return ResponseEntity.noContent().build();
    }
    // PATCH /categories/{id}/enable - Reactivar una categoría eliminada
    @PatchMapping("/{id}/enable")
    public ResponseEntity<CategoryResponse> enableCategory(@PathVariable Long id) {
        log.info("PATCH /categories/{}/enable - Reactivando categoría", id);
        return ResponseEntity.ok(categoryService.enableCategory(id));
    }

}
