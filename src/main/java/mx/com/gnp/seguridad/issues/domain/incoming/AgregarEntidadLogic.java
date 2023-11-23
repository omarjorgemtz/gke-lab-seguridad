package mx.com.gnp.seguridad.issues.domain.incoming;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.incoming.error.EntidadExistsException;

public interface AgregarEntidadLogic {

    /**
     * Agrega una nueva Entidad.
     *
     * @param entidad {@link Entidad} a agregar
     * @throws EntidadExistsException Si una Entidad con el mismo nombre ya existe
     */
    void agregar(Entidad entidad) throws EntidadExistsException;

    /**
     * Agrega una nueva Entidad.
     *
     * @param entidad {@link Entidad} a agregar
     * @return Entidad.
     * @throws EntidadExistsException Si una Entidad con el mismo nombre ya existe
     */
    Entidad agregarExterna(Entidad entidad) throws EntidadExistsException;

}
