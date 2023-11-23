package mx.com.gnp.seguridad.issues;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import mx.com.gnp.seguridad.Application;
import mx.com.gnp.seguridad.config.CuentasSecurityTestConfig;
import mx.com.gnp.errors.application.model.ErrorEntity;

@SpringJUnitConfig(CuentasSecurityTestConfig.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
	@Sql(scripts = "entidades.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
	@Sql(scripts = "entidades-clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
@ActiveProfiles("test")
public class EliminarEntidadSpec {
	
	@Autowired
	private TestRestTemplate rest;
	
	@Test
	public void eliminarTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";

		// when
		ResponseEntity<Void> response = rest.exchange("/entidades/" + id, HttpMethod.DELETE, null, Void.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<ErrorEntity> error = rest.getForEntity("/entidades/" + id, ErrorEntity.class);
		assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void eliminarNotFoundTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000000";

		// when
		ResponseEntity<ErrorEntity> response = rest.exchange("/entidades/" + id, HttpMethod.DELETE, null, ErrorEntity.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().getError()).isEqualTo("not_found");
		assertThat(response.getBody().getMensaje()).isEqualTo("No se encontró el recurso solicitado");
	}

}
