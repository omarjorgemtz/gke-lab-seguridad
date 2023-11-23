package mx.com.gnp.seguridad.issues.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Entity
@Table(name = "APP_ENTIDAD")
public class Entidad {

    /**
     * Identificador.
     */
    @Id
    @Column(name = "ID_ENTIDAD")
    @Schema(description = "Identificador de Entidad", example = "ID10")
    private String id;

    /**
     * Nombre.
     */
    @Column(name = "ENTIDAD")
    @Schema(description = "Nombre de Entidad", example = "Perro")
    private String nombre;

    /**
     * Descripción.
     */
    @Column(name = "DESCRIPCION")
    @Schema(type = "string", description = "Descripción de Entidad", example = "El perro es uno de los animales domésticos más antiguos del mundo.")
    private String descripcion;

}
