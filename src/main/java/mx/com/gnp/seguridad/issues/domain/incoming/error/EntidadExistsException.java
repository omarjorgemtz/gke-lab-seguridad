package mx.com.gnp.seguridad.issues.domain.incoming.error;

import mx.com.gnp.errors.domain.incoming.BusinessLogicException;

public class EntidadExistsException extends BusinessLogicException {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -2438248644051707684L;

    /**
     * @param message Mensaje de la excepción.
     */
    public EntidadExistsException(final String message) {
        super(message);
    }

}
