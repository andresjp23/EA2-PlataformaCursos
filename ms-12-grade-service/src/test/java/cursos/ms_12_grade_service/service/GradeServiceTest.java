package cursos.ms_12_grade_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cursos.ms_12_grade_service.dto.GradeRequest;
import cursos.ms_12_grade_service.dto.GradeResponse;
import cursos.ms_12_grade_service.model.entity.Grade;
import cursos.ms_12_grade_service.repository.GradeRepository;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeService gradeService;

    @Test
    void deberiaCrearNota_CuandoNoExisteDuplicado() {
        GradeRequest request = GradeRequest.builder()
                .studentId(1L).evaluationId(1L).score(85).build();
        when(gradeRepository.existsByStudentIdAndEvaluationIdAndIsActiveTrue(1L, 1L)).thenReturn(false);
        when(gradeRepository.save(any(Grade.class))).thenAnswer(i -> {
            Grade g = i.getArgument(0);
            g.setId(1L);
            g.setTakenAt(LocalDateTime.now());
            return g;
        });

        GradeResponse response = gradeService.createGrade(request);

        assertNotNull(response);
        assertEquals(Integer.valueOf(85), response.getScore());
        assertEquals(1L, response.getStudentId());
        verify(gradeRepository).existsByStudentIdAndEvaluationIdAndIsActiveTrue(1L, 1L);
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoDuplicado() {
        GradeRequest request = GradeRequest.builder()
                .studentId(1L).evaluationId(1L).score(85).build();
        when(gradeRepository.existsByStudentIdAndEvaluationIdAndIsActiveTrue(1L, 1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> gradeService.createGrade(request));
        assertEquals("El estudiante ya tiene una nota registrada para esta evaluacion.", ex.getMessage());
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void deberiaRetornarNota_CuandoExistePorId() {
        Grade grade = Grade.builder().id(1L).studentId(1L).evaluationId(1L).score(90)
                .takenAt(LocalDateTime.now()).isActive(true).build();
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        GradeResponse response = gradeService.getGradeById(1L);

        assertEquals(Integer.valueOf(90), response.getScore());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoNotaNoExiste() {
        when(gradeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gradeService.getGradeById(99L));
    }

    @Test
    void deberiaRetornarNota_CuandoExistePorStudentYEvaluation() {
        Grade grade = Grade.builder().id(1L).studentId(1L).evaluationId(1L).score(75).isActive(true).build();
        when(gradeRepository.findByStudentIdAndEvaluationIdAndIsActiveTrue(1L, 1L))
                .thenReturn(Optional.of(grade));

        GradeResponse response = gradeService.getGradeByStudentAndEvaluation(1L, 1L);

        assertEquals(Integer.valueOf(75), response.getScore());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoNoExisteStudentYEvaluation() {
        when(gradeRepository.findByStudentIdAndEvaluationIdAndIsActiveTrue(99L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gradeService.getGradeByStudentAndEvaluation(99L, 99L));
    }

    @Test
    void deberiaRetornarTodasLasNotasDeUnEstudiante() {
        when(gradeRepository.findByStudentIdAndIsActiveTrue(1L)).thenReturn(List.of(
                Grade.builder().id(1L).studentId(1L).evaluationId(1L).score(80).isActive(true).build(),
                Grade.builder().id(2L).studentId(1L).evaluationId(2L).score(90).isActive(true).build()));

        List<GradeResponse> result = gradeService.getGradesByStudent(1L);

        assertEquals(2, result.size());
    }
}
