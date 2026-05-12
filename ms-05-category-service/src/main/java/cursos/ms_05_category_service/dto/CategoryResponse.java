package cursos.ms_05_category_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Si un campo es null, NO aparece en el JSON
public class CategoryResponse {

    /*
        DTO DE SALIDA (Response)
        Se usa para devolver datos al cliente despues de realizar una operacion exitosa.
    */

    private Long id;           // ID de la categoría en nuestra BD
    private String name;       // Nombre de la categoría
    private String description; // Descripción
    private String icon;       // Icono (puede ser null si no tiene)
    private boolean active;    // Si está activa o inactiva (soft delete)

}
