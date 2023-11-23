package mx.com.gnp.seguridad.issues;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import mx.com.gnp.seguridad.Application;
import mx.com.gnp.seguridad.config.CuentasSecurityTestConfig;
import mx.com.gnp.seguridad.issues.application.model.EntidadCommand;
import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.errors.application.model.DataErrorEntity;
import mx.com.gnp.errors.application.model.ErrorEntity;

@SpringJUnitConfig(CuentasSecurityTestConfig.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@SqlGroup({ 
	@Sql(scripts = "entidades.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
	@Sql(scripts = "entidades-clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD) 
})
public class AgregarEntidadSpec {

	@Autowired
	private TestRestTemplate rest;

	@Test
	public void agregarTest() {

		// given
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad");
		request.setDescripcion("Descripción");

		// when
		ResponseEntity<Entidad> response = rest.postForEntity("/entidades", request, Entidad.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody().getId()).isNotBlank();
		assertThat(response.getBody().getNombre()).isEqualTo(request.getNombre());
		assertThat(response.getBody().getDescripcion()).isEqualTo(request.getDescripcion());
	}

	@Test
	public void agregarErrorEntidadExistsTest() {

		// given
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad-01");
		request.setDescripcion("Descripcion");

		// when
		ResponseEntity<ErrorEntity> response = rest.postForEntity("/entidades", request, ErrorEntity.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody().getError()).isEqualTo("entidad_exists");
		assertThat(response.getBody().getMensaje()).isEqualTo("El nombre '" + request.getNombre() + "' ya existe.");
	}

	@Test
	public void agregarErrorDataTest() {

		// given
		EntidadCommand request = new EntidadCommand();
		request.setNombre(null);
		request.setDescripcion("");

		// when
		ResponseEntity<DataErrorEntity> response = rest.postForEntity("/entidades", request, DataErrorEntity.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(response.getBody().getError()).isEqualTo("invalid_data");
		assertThat(response.getBody().getCampos())
				.anyMatch(c -> "nombre".equals(c.getCampo()) && "not_blank".equals(c.getError()))
				.anyMatch(c -> "descripcion".equals(c.getCampo()) && "not_blank".equals(c.getError())).size()
				.isEqualTo(2);
	}

}
