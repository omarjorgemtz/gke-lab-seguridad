package mx.com.gnp.seguridad.issues.domain.incoming;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.incoming.error.EntidadExistsException;

public interface ModificarEntidadLogic {

    /**
     * Modifica un Entidad.
     *
     * @param entidad Entidad a modificar
     * @throws EntidadExistsException Si ya existe un entidad con el mismo nombre
     */
    void modificar(Entidad entidad) throws EntidadExistsException;

}
