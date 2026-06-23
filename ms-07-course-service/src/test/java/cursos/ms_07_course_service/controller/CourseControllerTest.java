package cursos.ms_07_course_service.controller;

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

import cursos.ms_07_course_service.dto.CourseRequest;
import cursos.ms_07_course_service.dto.CourseResponse;
import cursos.ms_07_course_service.service.CourseService;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deberiaRetornar201_CuandoCrearCurso() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Java").description("Curso basico").build();
        CourseResponse response = CourseResponse.builder()
                .id(1L).title("Java").description("Curso basico").active(true).build();
        when(courseService.createCourse(any(CourseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java"));
    }

    @Test
    void deberiaRetornar400_CuandoRequestInvalido() throws Exception {
        CourseRequest request = CourseRequest.builder().build();

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar200_CuandoObtenerTodos() throws Exception {
        when(courseService.getAllCourses()).thenReturn(List.of(
                CourseResponse.builder().id(1L).title("A").build()));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorId() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(
                CourseResponse.builder().id(1L).title("Java").build());

        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java"));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorCategoria() throws Exception {
        when(courseService.getCoursesByCategory(1L)).thenReturn(List.of(
                CourseResponse.builder().id(1L).title("A").build()));

        mockMvc.perform(get("/courses/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoObtenerPorInstructor() throws Exception {
        when(courseService.getCoursesByInstructor(1L)).thenReturn(List.of(
                CourseResponse.builder().id(1L).title("A").build()));

        mockMvc.perform(get("/courses/instructor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deberiaRetornar200_CuandoActualizar() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .categoryId(1L).instructorId(1L).title("Actualizado").description("X").build();
        when(courseService.updateCourse(any(Long.class), any(CourseRequest.class)))
                .thenReturn(CourseResponse.builder().id(1L).title("Actualizado").build());

        mockMvc.perform(put("/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Actualizado"));
    }

    @Test
    void deberiaRetornar204_CuandoEliminar() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar200_CuandoReactivar() throws Exception {
        when(courseService.enableCourse(1L))
                .thenReturn(CourseResponse.builder().id(1L).title("X").active(true).build());

        mockMvc.perform(patch("/courses/1/enable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
}
