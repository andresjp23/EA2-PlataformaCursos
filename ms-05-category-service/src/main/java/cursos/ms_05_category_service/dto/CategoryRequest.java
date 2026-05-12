package cursos.ms_05_category_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    /*
        DTO DE ENTRADA (Request)
        (Se usa en los metodos POST Y PUT para recibir los datos del cliente)
        
        VALIDACIONES:
        - @NotBlank: el campo no puede ser null, vacio, ni solo espacios
        - @Size: limita la cantidad de caracteres (opcional, para evitar datos demasiados largos)
        */

    // Nombre de la categoria
    @NotBlank(message = "El nombre no puede estar vacio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String name;

    // Descripcion de la categoria
    @NotBlank(message = "La descripcion no puede estar vacia.")
    @Size(min = 50, max = 500, message = "La descripcion debe tener entre 50 y 500 caracteres.")
    private String description;

    // Icono opcional (puede ser emoji, o URL de una imagen)
    private String icon;


}
