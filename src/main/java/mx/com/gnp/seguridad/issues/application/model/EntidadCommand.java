package mx.com.gnp.seguridad.issues.application.model;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Command Entidad.
 */
@Data
public class EntidadCommand {

    /**
     * Nombre de la entidad.
     */
    @Schema(description = "Nombre de la Entidad", example = "Perro")
    @NotBlank
    private String nombre;

    /**
     * Descripción de la entidad.
     */
    @Schema(description = "Descripción de la Entidad", example = "El perro es uno de los animales domésticos más antiguos del mundo.")
    @NotBlank
    private String descripcion;

}
