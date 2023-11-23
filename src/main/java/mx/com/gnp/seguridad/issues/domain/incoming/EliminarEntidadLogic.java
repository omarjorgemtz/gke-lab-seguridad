package mx.com.gnp.seguridad.issues.domain.incoming;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.incoming.error.EntidadExistsException;

public interface EliminarEntidadLogic {

    /**
     * Elimina un Entidad.
     *
     * @param entidad {@link Entidad} a eliminar
     */
    void eliminar(Entidad entidad);

    /**
     * Agrega una nueva Entidad.
     *
     * @param entidad {@link Entidad} a agregar
     * @throws EntidadExistsException Si una Entidad con el mismo nombre ya existe
     */
    void eliminarExterna(Entidad entidad) throws EntidadExistsException;

}
