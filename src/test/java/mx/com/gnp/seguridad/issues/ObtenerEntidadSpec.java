package mx.com.gnp.seguridad.issues;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import mx.com.gnp.seguridad.Application;
import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.errors.application.model.ErrorEntity;

@SpringJUnitConfig
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ObtenerEntidadSpec {
	
	@Autowired
	private TestRestTemplate rest;
	
	@Test
	@SqlGroup({
		@Sql(scripts = "entidades.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "entidades-clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	})
	public void obtenerAllTest() {
		
		// when
		ResponseEntity<List<Entidad>> response = rest.exchange(
				"/entidades", HttpMethod.GET, null,
	            new ParameterizedTypeReference<List<Entidad>>() {});
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().size()).isEqualTo(15);
		for(int i=0; i < response.getBody().size(); i++) {
			assertThat(response.getBody().get(i).getId()).isEqualTo("00000000-0000-0000-0000-0000000000" + String.format("%02d", i + 1));
			assertThat(response.getBody().get(i).getNombre()).isEqualTo("Entidad-" + String.format("%02d", i + 1));
			assertThat(response.getBody().get(i).getDescripcion()).isEqualTo("Descripcion-" + String.format("%02d", i + 1));
		}		
	}
	
	@Test
	public void obtenerAllEmptyTest() {
		
		// when
		ResponseEntity<List<Entidad>> response = rest.exchange(
				"/entidades", HttpMethod.GET, null,
	            new ParameterizedTypeReference<List<Entidad>>() {});
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().size()).isEqualTo(0);
	}
	
	@Test
	@SqlGroup({
		@Sql(scripts = "entidades.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
		@Sql(scripts = "entidades-clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
	})
	public void obtenerByIdTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";

		// when
		ResponseEntity<Entidad> response = rest.getForEntity("/entidades/" + id, Entidad.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getId()).isEqualTo(id);
		assertThat(response.getBody().getNombre()).isEqualTo("Entidad-01");
		assertThat(response.getBody().getDescripcion()).isEqualTo("Descripcion-01");
	}
	
	@Test
	public void obtenerByIdNotFoundTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000000";

		// when		
		ResponseEntity<ErrorEntity> response = rest.getForEntity("/entidades/" + id, ErrorEntity.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().getError()).isEqualTo("not_found");
		assertThat(response.getBody().getMensaje()).isEqualTo("No se encontró el recurso solicitado");
	}

}
