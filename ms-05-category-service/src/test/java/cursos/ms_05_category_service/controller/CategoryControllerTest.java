package cursos.ms_05_category_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cursos.ms_05_category_service.dto.CategoryRequest;
import cursos.ms_05_category_service.dto.CategoryResponse;
import cursos.ms_05_category_service.service.CategoryService;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCrearCategoria() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Programacion").description("Descripcion larga ".repeat(5)).build();
        CategoryResponse response = CategoryResponse.builder()
                .id(1L).name("Programacion").description("Descrip larga").active(true).build();
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Programacion"));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        CategoryRequest request = CategoryRequest.builder().name("A").description("Short").build();

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodasActivas() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(
                CategoryResponse.builder().id(1L).name("Java").active(true).build()));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodasAdmin() throws Exception {
        when(categoryService.getAllCategoriesIncludingInactive()).thenReturn(List.of(
                CategoryResponse.builder().id(1L).name("A").active(true).build(),
                CategoryResponse.builder().id(2L).name("B").active(false).build()));

        mockMvc.perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorId() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(
                CategoryResponse.builder().id(1L).name("Java").build());

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void deberiaRetornar200_CuandoActualizar() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Nuevo").description("Descripcion actualizada ".repeat(5)).build();
        when(categoryService.updateCategory(any(Long.class), any(CategoryRequest.class)))
                .thenReturn(CategoryResponse.builder().id(1L).name("Nuevo").build());

        mockMvc.perform(put("/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo"));
    }

    @Test
    void deberiaRetornar204_CuandoEliminar() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar200_CuandoReactivar() throws Exception {
        when(categoryService.enableCategory(1L))
                .thenReturn(CategoryResponse.builder().id(1L).name("X").active(true).build());

        mockMvc.perform(patch("/categories/1/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
}
