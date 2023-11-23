package mx.com.gnp.seguridad.issues.domain.outgoing;

import java.util.List;
import java.util.Optional;

import mx.com.gnp.seguridad.issues.domain.Entidad;

public interface EntidadRepository {

    /**
     * Agrega o modifica en el repositorio.
     *
     * @param entidad {@link Entidad}
     */
    void save(Entidad entidad);

    /**
     * Elimina del repositorio.
     *
     * @param entidad {@link Entidad}
     */
    void delete(Entidad entidad);

    /**
     * Busca todos los elementos y los ordena por nombre.
     *
     * @return Lista de {@link Entidad}
     */
    List<Entidad> findByOrderByNombre();

    /**
     * Busca en el repositorio por el identificador.
     *
     * @param id Identificador.
     * @return {@link Entidad}
     */
    Optional<Entidad> findById(String id);

    /**
     * Busca si existe una entidad con el nombre.
     *
     * @param nombre Nombre
     * @return Si existe
     */
    boolean existsByNombre(String nombre);

    /**
     * Busca si existe una entidad con el mismo nombre pero no con el identificador.
     *
     * @param nombre Nombre
     * @param id     Identificador
     * @return Si existe
     */
    boolean existsByNombreAndNotId(String nombre, String id);

}
