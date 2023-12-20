package mx.com.gnp.seguridad.issues.application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.EntidadList;
import mx.com.gnp.seguridad.issues.domain.incoming.AgregarEntidadLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.gnp.seguridad.issues.application.mapper.EntidadMapper;
import mx.com.gnp.seguridad.issues.application.model.EntidadCommand;
import mx.com.gnp.seguridad.issues.domain.incoming.EliminarEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.incoming.ModificarEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.incoming.ObtenerEntidadLogic;
import mx.com.gnp.errors.application.ResourceNotFoundException;
import mx.com.gnp.errors.application.model.DataErrorEntity;
import mx.com.gnp.errors.application.model.ErrorEntity;

@Tag(name = "Entidad")
@RestController
@RequestMapping("/entidades")
public class EntidadController {

    /**
     * Lógica para agregar entidades.
     */
    @Autowired
    private AgregarEntidadLogic agregarLogic;

    /**
     * Lógica para modificar entidades.
     */
    @Autowired
    private ModificarEntidadLogic modificarLogic;

    /**
     * Lógica para eliminar entidades.
     */
    @Autowired
    private EliminarEntidadLogic eliminarLogic;

    /**
     * Lógica para obtener entidades.
     */
    @Autowired
    private ObtenerEntidadLogic obtenerLogic;

    /**
     * Mappper de {@link EntidadMapper}.
     */
    @Autowired
    private EntidadMapper entidadMapper;

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Obtiene todas las Entidad")
    @ApiResponse(responseCode = "200", description = "Lista de Entidad")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Entidad> list() {
        return obtenerLogic.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Obtiene una entidad")
    @ApiResponse(responseCode = "200", description = "Entidad")
    @ApiResponse(responseCode = "404", description = "No existe el entidad", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<Entidad> get(final @PathVariable String id) {
        return obtenerLogic.getById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Crea una Entidad")
    @ApiResponse(responseCode = "201", description = "Entidad creada")
    @ApiResponse(responseCode = "409", description = "La entidad ya existe", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ApiResponse(responseCode = "422", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = DataErrorEntity.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Entidad add(final @Valid @RequestBody EntidadCommand request) {
        Entidad entidad = entidadMapper.toEntidad(request);
        agregarLogic.agregar(entidad);
        return entidad;
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Actualiza una Entidad")
    @ApiResponse(responseCode = "200", description = "Entidad actualizada")
    @ApiResponse(responseCode = "404", description = "No existe la entidad", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ApiResponse(responseCode = "409", description = "La entidad ya existe", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ApiResponse(responseCode = "422", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = DataErrorEntity.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void modify(final @PathVariable String id, final @Valid @RequestBody EntidadCommand request) {
        Entidad entidad = get(id).orElseThrow(() -> new ResourceNotFoundException());
        entidadMapper.merge(request, entidad);
        modificarLogic.modificar(entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Elimina una Entidad")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "No existe el entidad", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(final @PathVariable String id) {
        Entidad entidad = get(id).orElseThrow(() -> new ResourceNotFoundException());
        eliminarLogic.eliminar(entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Consulta un catálogo con restTemplate")
    @ApiResponse(responseCode = "200", description = "Entidades externas")
    @ApiResponse(responseCode = "404", description = "No se encontraron entidades", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @GetMapping(value = "/externas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntidadList> getEntidadesExternas(final @PathVariable String id) {
        ResponseEntity<EntidadList> response = new ResponseEntity<>(obtenerLogic.getAllExterna(), HttpStatus.OK);
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Crea una Entidad")
    @ApiResponse(responseCode = "201", description = "Entidad externa creada")
    @ApiResponse(responseCode = "409", description = "La entidad ya existe", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ApiResponse(responseCode = "422", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = DataErrorEntity.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/externas", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Entidad addExterna(final @RequestBody EntidadCommand request) {
        Entidad entidad = entidadMapper.toEntidad(request);
        entidad.setDescripcion("Nueva descripcion");
        agregarLogic.agregarExterna(entidad);
        return entidad;
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Consulta un catálogo con restTemplate")
    @ApiResponse(responseCode = "200", description = "Entidades externas")
    @ApiResponse(responseCode = "404", description = "No se encontraron entidades", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @GetMapping(value = "/query/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getQueryEntidad(final @PathVariable String id) {
        return obtenerLogic.getQueryEntidad(id);
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Consulta un catálogo con restTemplate")
    @ApiResponse(responseCode = "200", description = "Entidades externas")
    @ApiResponse(responseCode = "404", description = "No se encontraron entidades", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @GetMapping(value = "/{nombre}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Entidad getEntidadByName(final @PathVariable String nombre) {

        Connection connection = null;
        Entidad entidadByName = null;
        try {
            // Cargar el driver de la base de datos HSQLDB
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            // Establecer la conexión con la base de datos (en este ejemplo, la base de datos se llama "testdb")
            connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");

            // Ejecutar la consulta
            String consulta = "SELECT * FROM entidad where nombre = '" + nombre + "' ";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            // Procesar los resultados de la consulta
            while (resultSet.next()) {
                entidadByName =  new Entidad();
                entidadByName.setId(resultSet.getString("id"));
                entidadByName.setNombre(resultSet.getString("nombre"));
                entidadByName.setDescripcion(resultSet.getString("descripcion"));
            }
            resultSet.close();
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return entidadByName;
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Elimina una Entidad Externa")
    @ApiResponse(responseCode = "200")
    @ApiResponse(responseCode = "404", description = "No existe el entidad", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/externas/{id}")
    public void deleteExterna(final @PathVariable String id) {
        Entidad entidad = get(id).orElseThrow(() -> new ResourceNotFoundException());
        eliminarLogic.eliminarExterna(entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Operation(summary = "Crea una Entidad y la consulta")
    @ApiResponse(responseCode = "201", description = "Entidad externa creada")
    @ApiResponse(responseCode = "409", description = "La entidad ya existe", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
    @ApiResponse(responseCode = "422", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = DataErrorEntity.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/externas/entidad", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Entidad addAndGetExterna(final @RequestBody EntidadCommand request) {
        Entidad entidad = entidadMapper.toEntidad(request);
        entidad.setDescripcion("Nueva descripcion");
        agregarLogic.agregarExterna(entidad);
        return entidad;
    }

}
