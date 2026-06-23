package cursos.ms_10_evaluation_service.service;

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

import cursos.ms_10_evaluation_service.client.CourseClient;
import cursos.ms_10_evaluation_service.client.UserClient;
import cursos.ms_10_evaluation_service.dto.EvaluationRequest;
import cursos.ms_10_evaluation_service.dto.EvaluationResponse;
import cursos.ms_10_evaluation_service.model.entity.Evaluation;
import cursos.ms_10_evaluation_service.repository.EvaluationRepository;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private CourseClient courseClient;

    @InjectMocks
    private EvaluationService evaluationService;

    @Test
    void deberiaCrearEvaluacion_CuandoTituloNoExisteYCursoValido() {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Examen Final").description("Evaluacion final")
                .maxScore(100).passingScore(70).build();
        when(evaluationRepository.existsByTitle("Examen Final")).thenReturn(false);
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(i -> {
            Evaluation e = i.getArgument(0);
            e.setId(1L);
            return e;
        });

        EvaluationResponse response = evaluationService.createEvaluation(request);

        assertNotNull(response);
        assertEquals("Examen Final", response.getTitle());
        assertEquals(Integer.valueOf(100), response.getMaxScore());
        assertEquals("PUBLISHED", response.getStatus());
        verify(courseClient).getCourseById(1L);
        verify(evaluationRepository).save(any(Evaluation.class));
    }

    @Test
    void deberiaUsarValoresPorDefecto_CuandoMaxScoreYPassingScoreSonNulos() {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Quiz").description("").build();
        when(evaluationRepository.existsByTitle("Quiz")).thenReturn(false);
        when(courseClient.getCourseById(1L)).thenReturn(new Object());
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(i -> {
            Evaluation e = i.getArgument(0);
            e.setId(2L);
            return e;
        });

        EvaluationResponse response = evaluationService.createEvaluation(request);

        assertEquals(Integer.valueOf(100), response.getMaxScore());
        assertEquals(Integer.valueOf(70), response.getPassingScore());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoTituloDuplicado() {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Dupe").description("X").build();
        when(evaluationRepository.existsByTitle("Dupe")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> evaluationService.createEvaluation(request));
        assertEquals("Ya existe una evaluacion con ese titulo.", ex.getMessage());
        verify(evaluationRepository, never()).save(any());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoCursoNoValido() {
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(99L).title("X").description("Y").build();
        when(evaluationRepository.existsByTitle("X")).thenReturn(false);
        when(courseClient.getCourseById(99L)).thenThrow(new RuntimeException("Curso no encontrado"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> evaluationService.createEvaluation(request));
        assertEquals("No se encontro el curso especificado.", ex.getMessage());
    }

    @Test
    void deberiaRetornarEvaluacion_CuandoExiste() {
        Evaluation evaluation = Evaluation.builder().id(1L).courseId(1L).title("Examen")
                .description("X").maxScore(100).passingScore(70).status("PUBLISHED").isActive(true).build();
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));

        EvaluationResponse response = evaluationService.getEvaluationById(1L);

        assertEquals("Examen", response.getTitle());
        assertEquals(Integer.valueOf(100), response.getMaxScore());
    }

    @Test
    void deberiaLanzarExcepcion_CuandoEvaluacionNoExiste() {
        when(evaluationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> evaluationService.getEvaluationById(99L));
    }

    @Test
    void deberiaRetornarSoloActivas_PorCurso() {
        when(evaluationRepository.findByCourseId(1L)).thenReturn(List.of(
                Evaluation.builder().id(1L).title("A").isActive(true).build(),
                Evaluation.builder().id(2L).title("B").isActive(false).build()));

        List<EvaluationResponse> result = evaluationService.getEvaluationsByCourseId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void deberiaRetornarSoloActivas_Todas() {
        when(evaluationRepository.findAll()).thenReturn(List.of(
                Evaluation.builder().id(1L).title("A").isActive(true).build(),
                Evaluation.builder().id(2L).title("B").isActive(false).build()));

        List<EvaluationResponse> result = evaluationService.getAllEvaluations();

        assertEquals(1, result.size());
    }

    @Test
    void deberiaActualizarEvaluacion() {
        Evaluation existing = Evaluation.builder().id(1L).courseId(1L).title("Viejo")
                .description("X").maxScore(100).passingScore(70).isActive(true).build();
        EvaluationRequest request = EvaluationRequest.builder()
                .courseId(1L).title("Nuevo").description("Y").maxScore(50).passingScore(30).build();
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(i -> i.getArgument(0));

        EvaluationResponse response = evaluationService.updateEvaluation(1L, request);

        assertEquals("Nuevo", response.getTitle());
        assertEquals(Integer.valueOf(50), response.getMaxScore());
    }

    @Test
    void deberiaRealizarSoftDelete() {
        Evaluation evaluation = Evaluation.builder().id(1L).title("X").isActive(true).build();
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));

        evaluationService.deleteEvaluation(1L);

        assertFalse(evaluation.isActive());
        verify(evaluationRepository).save(evaluation);
    }
}
