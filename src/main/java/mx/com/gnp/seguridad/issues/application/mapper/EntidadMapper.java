package mx.com.gnp.seguridad.issues.application.mapper;

import mx.com.gnp.seguridad.issues.application.model.EntidadCommand;
import mx.com.gnp.seguridad.issues.domain.Entidad;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class EntidadMapper {

    /**
     * Convierte un {@link EntidadCommand} a {@link Entidad}.
     *
     * @param command {@link EntidadCommand}
     * @return {@link Entidad}
     */
    public Entidad toEntidad(final EntidadCommand command) {
        Entidad entidad = new Entidad();
        merge(command, entidad);
        return entidad;
    }

    /**
     * Coloca los valores de las propiedades de un {@link EntidadCommand} dentro de
     * las propiedades de un {@link Entidad}.
     *
     * @param source Propiedades origen
     * @param target Propiedades destino
     */
    public void merge(final EntidadCommand source, final Entidad target) {
        BeanUtils.copyProperties(source, target);
    }

}
