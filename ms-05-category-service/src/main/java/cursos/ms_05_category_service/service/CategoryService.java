package cursos.ms_05_category_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cursos.ms_05_category_service.dto.CategoryRequest;
import cursos.ms_05_category_service.dto.CategoryResponse;
import cursos.ms_05_category_service.model.entity.Category;
import cursos.ms_05_category_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // CREATE - Crear una nueava categoria
    public CategoryResponse createCategory (CategoryRequest request){
        log.info("Iniciando proceso de creacion de categoria con nombre: {}", request.getName());

        // Verificamos que no exista una categoria con el mismo nombre
        if (categoryRepository.findByName(request.getName()).isPresent()){
            log.warn("Intento de crear una categoria con nombre duplicado: {}", request.getName());
            throw new RuntimeException("Ya existe una caetgoria con ese nombre.");
        }

        // Construimos la entidad Category desde el DTO request
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .isActive(true) // Por defecto, la categoria se crea activa
                .build();
            
        // Guardamos la categoria en la base de datos
        Category saved = categoryRepository.save(category);
        log.info("Categoria creada exitosamente con ID: {}", saved.getId());

        return toResponse(saved);
    }

    // READ ONE - Obtener una categoria por su id
    public CategoryResponse getCategoryById(Long id){
        log.info("Buscando categoria con ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria no econtrada: {}", id);
                    return new RuntimeException("Categoria no encontrada");
                });
        log.debug("Categoria encontrada: {}", category.getName());
        return toResponse(category);
    }

    // READ ALL - Obtener todas las categorías activas
    public List<CategoryResponse> getAllCategories() {
        log.info("Obteniendo todas las categorías activas");
        
        // findAll() de JpaRepository devuelve todas las filas
        // filter() deja solo las activas
        // map() convierte cada Category a CategoryResponse
        return categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // READ ALL - Obtener todas las categorías (incluyendo inactivas) - útil para ADMIN
    public List<CategoryResponse> getAllCategoriesIncludingInactive() {
        log.info("Obteniendo todas las categorías (incluyendo inactivas)");
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE - Actualizar una categoría existente
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Actualizando categoría con ID: {}", id);
        
        // Primero verificamos que exista
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada para actualizar con ID: {}", id);
                    return new RuntimeException("Categoría no encontrada.");
                });
        // Verificamos que el nuevo nombre no esté duplicado (si cambió el nombre)
        if (!category.getName().equals(request.getName())) {
            if (categoryRepository.findByName(request.getName()).isPresent()) {
                log.warn("Intento de actualizar a nombre duplicado: {}", request.getName());
                throw new RuntimeException("Ya existe una categoría con ese nombre.");
            }
        }
        // Actualizamos los campos (el ID no se toca)
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        Category updated = categoryRepository.save(category);
        log.info("Categoría actualizada exitosamente con ID: {}", id);
        
        return toResponse(updated);
    }

    // DELETE (SOFT) - Marcar categoría como inactiva
    public void deleteCategory(Long id) {
        log.info("Eliminando (soft delete) categoría con ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada para eliminar con ID: {}", id);
                    return new RuntimeException("Categoría no encontrada.");
                });
        // Soft delete: solo cambiamos el flag isActive a false
        category.setActive(false);
        categoryRepository.save(category);
        
        log.info("Categoría eliminada (soft delete) exitosamente con ID: {}", id);
    }

    // ENABLE - Reactivar una categoría (útil para admins)
    public CategoryResponse enableCategory(Long id) {
        log.info("Reactivando categoría con ID: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoría no encontrada para reactivar con ID: {}", id);
                    return new RuntimeException("Categoría no encontrada.");
                });
        category.setActive(true);
        Category enabled = categoryRepository.save(category);
        
        log.info("Categoría reactivada exitosamente con ID: {}", id);
        return toResponse(enabled);
    }

    // Converter: Convierte entidad Category → DTO CategoryResponse
    // Se usa al final de cada método que devuelve datos al cliente
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .active(category.isActive())
                .build();
    }

}
