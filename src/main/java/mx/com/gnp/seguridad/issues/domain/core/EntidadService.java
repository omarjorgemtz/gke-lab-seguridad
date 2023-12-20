package mx.com.gnp.seguridad.issues.domain.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.seguridad.issues.domain.EntidadList;
import mx.com.gnp.seguridad.issues.domain.incoming.error.EntidadExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import mx.com.gnp.seguridad.issues.domain.incoming.AgregarEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.incoming.EliminarEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.incoming.ModificarEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.incoming.ObtenerEntidadLogic;
import mx.com.gnp.seguridad.issues.domain.outgoing.EntidadRepository;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Service
public class EntidadService implements AgregarEntidadLogic, ModificarEntidadLogic, EliminarEntidadLogic, ObtenerEntidadLogic {

    @Value("${gnp.apis-externas.url-entidades}")
    private String urlEntidades;

    @Value("${gnp.clave-aes}")
    private String claveAes;

    /**
     * Repository para entidades.
     */
    @Autowired
    private EntidadRepository repository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Entidad> getById(final String id) {
        return repository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Entidad> getAll() {
        return repository.findByOrderByNombre();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void eliminar(final Entidad entidad) {
        log.info("Entidad eliminado: {}", entidad.getId());
        repository.delete(entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void modificar(final Entidad entidad) {
        log.debug("Modificando Entidad: {}", entidad);
        if (repository.existsByNombreAndNotId(entidad.getNombre(), entidad.getId())) {
            log.info("Entidad existente: {}", entidad.getNombre());
            throw new EntidadExistsException("El nombre '" + entidad.getNombre() + "' ya existe.");
        }
        repository.save(entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void agregar(final Entidad entidad) {
        log.debug("Agregando Entidad: {}", entidad);
        if (repository.existsByNombre(entidad.getNombre())) {
            log.info("Entidad existente: {}", entidad.getNombre());
            throw new EntidadExistsException("El nombre '" + entidad.getNombre() + "' ya existe.");
        }
        entidad.setId(UUID.randomUUID().toString());
        repository.save(entidad);
        log.debug("Entidad nueva: {}", entidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entidad agregarExterna(final Entidad entidad) {
        entidad.setDescripcion(cifradoTextoAes(entidad.getDescripcion()));
        log.debug("Agregando Entidad externa: {}", entidad);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Entidad> request = new HttpEntity<>(entidad);
        Entidad entidadNueva = restTemplate.postForObject(urlEntidades, request, Entidad.class, headers);
        return addExternoInfo(entidadNueva);
    }

    private Entidad addExternoInfo(final Entidad entidad) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Entidad> request = new HttpEntity<>(entidad);
        return restTemplate.postForObject(urlEntidades, request, Entidad.class, headers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminarExterna(final Entidad entidad) {
        log.debug("Agregando Entidad externa: {}", entidad);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Entidad> request = new HttpEntity<>(entidad);
        ResponseEntity<Entidad> entidadEliminada = restTemplate.exchange(urlEntidades, HttpMethod.DELETE, request,
                new ParameterizedTypeReference<Entidad>() {
                }, headers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntidadList getAllExterna() {
        RestTemplate restTemplate = new RestTemplate();
        EntidadList listEntidad = restTemplate.getForObject(urlEntidades, EntidadList.class);
        assert listEntidad != null;
        for (Entidad entidad : listEntidad.getEntidadList()) {
            Optional<Entidad> entidadDB = getById(entidad.getId());
            if (entidadDB.isPresent()) {
                entidad.setDescripcion(getQueryEntidad(entidadDB.get().getId()));
            } else {
                agregar(entidad);
            }
        }
        return listEntidad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQueryEntidad(final String idEntidad) {

        Connection connection = null;
        try {
            // Cargar el driver de la base de datos HSQLDB
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            // Establecer la conexión con la base de datos (en este ejemplo, la base de datos se llama "testdb")
            connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "");

            // Ejecutar la consulta
            String consulta = "SELECT * FROM entidad where id = '" + idEntidad + "' ";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            // Procesar los resultados de la consulta
            while (resultSet.next()) {
                // Aquí puedes recuperar los datos de cada fila, por ejemplo:
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");

                // Hacer algo con los datos recuperados, por ejemplo, imprimirlos
                System.out.println("ID: " + id + ", Nombre: " + nombre);
            }

            // Cerrar recursos
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
        return "select * from entidad where id = '" + idEntidad + "' ";
    }

    private String cifradoTextoAes(final String texto) {
        String textoCifradoBase64 = "";
        try {
            // Crear un objeto SecretKeySpec con la clave
            SecretKeySpec key = new SecretKeySpec(claveAes.getBytes(), "AES");

            // Inicializar el cifrado con AES
            Cipher cipher = Cipher.getInstance("AES");

            // Cifrado
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] textoCifrado = cipher.doFinal(texto.getBytes());
            textoCifradoBase64 = Base64.getEncoder().encodeToString(textoCifrado);
            log.debug("Texto cifrado: {}", textoCifradoBase64);

            // Descifrado
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] textoDescifrado = cipher.doFinal(Base64.getDecoder().decode(textoCifradoBase64));
            String textoOriginalRecuperado = new String(textoDescifrado);
            log.debug(textoOriginalRecuperado);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
        return textoCifradoBase64;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Entidad> getEntidadesById(final String id){
        Connection connection = null;
        List<Entidad> entidadById = new ArrayList<>();
        try {
            // Cargar el driver de la base de datos HSQLDB
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establecer la conexión con la base de datos (en este ejemplo, la base de datos se llama "testdb")
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_database", "root", "password");

            // Ejecutar la consulta
            String consulta = "SELECT * FROM APP_ENTIDAD where ID_ENTIDAD = '" + id + "' ";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            // Procesar los resultados de la consulta
            while (resultSet.next()) {
                Entidad entidadByName =  new Entidad();
                entidadByName.setId(resultSet.getString("ID_ENTIDAD"));
                entidadByName.setNombre(resultSet.getString("ENTIDAD"));
                entidadByName.setDescripcion(resultSet.getString("DESCRIPCION"));
                entidadById.add(entidadByName);
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
        return entidadById;
    }

}
