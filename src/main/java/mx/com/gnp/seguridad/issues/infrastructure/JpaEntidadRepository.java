package mx.com.gnp.seguridad.issues.infrastructure;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import mx.com.gnp.seguridad.issues.domain.outgoing.EntidadRepository;

public interface JpaEntidadRepository extends EntidadRepository, Repository<Entidad, String> {

    /**
     * {@inheritDoc}
     */
    @Query("select count(m) > 0 from Entidad m where m.nombre = :nombre")
    boolean existsByNombre(@Param("nombre") String nombre);

    /**
     * {@inheritDoc}
     */
    @Query("select count(m) > 0 from Entidad m where m.nombre = :nombre and m.id != :id")
    boolean existsByNombreAndNotId(@Param("nombre") String nombre, @Param("id") String id);

}
