package cursos.ms_05_category_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cursos.ms_05_category_service.dto.CategoryRequest;
import cursos.ms_05_category_service.dto.CategoryResponse;
import cursos.ms_05_category_service.model.entity.Category;
import cursos.ms_05_category_service.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void deberiaCrearCategoria_CuandoNombreNoExiste() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Programacion").description("Descripcion larga ".repeat(5)).icon("💻").build();
        when(categoryRepository.findByName("Programacion")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> {
            Category c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        CategoryResponse response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals("Programacion", response.getName());
        assertTrue(response.isActive());
        verify(categoryRepository).findByName("Programacion");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoNombreDuplicado() {
        CategoryRequest request = CategoryRequest.builder().name("Prog").description("X".repeat(60)).build();
        when(categoryRepository.findByName("Prog")).thenReturn(Optional.of(new Category()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> categoryService.createCategory(request));
        assertEquals("Ya existe una caetgoria con ese nombre.", ex.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deberiaRetornarCategoria_CuandoExiste() {
        Category category = Category.builder().id(1L).name("Java").description("X").isActive(true).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getCategoryById(1L);

        assertEquals("Java", response.getName());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoCategoriaNoExiste() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void deberiaRetornarSoloActivas() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                Category.builder().id(1L).name("A").description("X").isActive(true).build(),
                Category.builder().id(2L).name("B").description("Y").isActive(false).build()));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getName());
    }

    @Test
    void deberiaRetornarTodas_IncluyendoInactivas() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                Category.builder().id(1L).name("A").description("X").isActive(true).build(),
                Category.builder().id(2L).name("B").description("Y").isActive(false).build()));

        List<CategoryResponse> result = categoryService.getAllCategoriesIncludingInactive();

        assertEquals(2, result.size());
    }

    @Test
    void deberiaActualizarCategoria_SinDuplicado() {
        Category existing = Category.builder().id(1L).name("Viejo").description("X").isActive(true).build();
        CategoryRequest request = CategoryRequest.builder().name("Nuevo").description("Descripcion larga ".repeat(5)).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Nuevo")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = categoryService.updateCategory(1L, request);

        assertEquals("Nuevo", response.getName());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoUpdateNombreDuplicado() {
        Category existing = Category.builder().id(1L).name("Viejo").description("X").isActive(true).build();
        CategoryRequest request = CategoryRequest.builder().name("Existente").description("X".repeat(60)).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Existente")).thenReturn(Optional.of(new Category()));

        assertThrows(RuntimeException.class, () -> categoryService.updateCategory(1L, request));
    }

    @Test
    void deberiaRealizarSoftDelete() {
        Category category = Category.builder().id(1L).name("X").description("Y").isActive(true).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertFalse(category.isActive());
        verify(categoryRepository).save(category);
    }

    @Test
    void deberiaReactivarCategoria() {
        Category category = Category.builder().id(1L).name("X").description("Y").isActive(false).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = categoryService.enableCategory(1L);

        assertTrue(response.isActive());
    }
}
