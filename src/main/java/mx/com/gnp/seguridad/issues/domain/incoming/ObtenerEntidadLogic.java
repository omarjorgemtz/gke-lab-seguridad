package mx.com.gnp.seguridad.issues.domain.incoming;

import java.util.List;
import java.util.Optional;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.EntidadList;

public interface ObtenerEntidadLogic {

    /**
     * Obtiene una Entidad por su identificador.
     *
     * @param id Identificador
     * @return {@link Entidad}
     */
    Optional<Entidad> getById(String id);

    /**
     * Obtiene todas las "Entidad" ordenadas por nombre.
     *
     * @return Lista de {@link Entidad}
     */
    List<Entidad> getAll();

    /**
     * Obtiene todas las "Entidad" externas ordenadas por nombre.
     *
     * @return Lista de {@link Entidad}
     */
    EntidadList getAllExterna();

    /**
     * Obtiene todas las "Entidad" externas ordenadas por nombre.
     *
     * @return Query de {@link Entidad}
     * @param  id Identificador
     */
    String getQueryEntidad(String id);

    /**
     * Obtiene todas las "Entidad" externas ordenadas por nombre.
     *
     * @return Query de {@link Entidad}
     * @param  id Identificador
     */
    List<Entidad> getEntidadesById(String id);

}
