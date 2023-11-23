package mx.com.gnp.seguridad.issues;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
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
import mx.com.gnp.seguridad.issues.application.model.EntidadCommand;
import mx.com.gnp.seguridad.issues.domain.Entidad;
import mx.com.gnp.errors.application.model.DataErrorEntity;
import mx.com.gnp.errors.application.model.ErrorEntity;

@SpringJUnitConfig(CuentasSecurityTestConfig.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlGroup({
	@Sql(scripts = "entidades.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
	@Sql(scripts = "entidades-clean.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
})
@ActiveProfiles("test")
public class ModificarEntidadSpec {
	
	@Autowired
	private TestRestTemplate rest;
	
	@Test
	public void modificarTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad-00");
		request.setDescripcion("Descripción");
		
		// when
		ResponseEntity<String> response = rest.exchange(
				"/entidades/" + id,
				HttpMethod.PUT,
				new HttpEntity<>(request),
				String.class);
		
		System.out.println(response.getBody());
		
		// then
		Entidad entidad = rest.getForObject("/entidades/" + id, Entidad.class);
		assertThat(entidad.getId()).isEqualTo(id);
		assertThat(entidad.getNombre()).isEqualTo(request.getNombre());
		assertThat(entidad.getDescripcion()).isEqualTo(request.getDescripcion());
	}
	
	@Test
	public void modificarSameTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad-01");
		request.setDescripcion("Descripción");
		
		// when
		rest.put("/entidades/" + id, request);
		
		// then
		Entidad entidad = rest.getForObject("/entidades/" + id, Entidad.class);
		assertThat(entidad.getId()).isEqualTo(id);
		assertThat(entidad.getNombre()).isEqualTo(request.getNombre());
		assertThat(entidad.getDescripcion()).isEqualTo(request.getDescripcion());
	}
	
	@Test
	public void modificarErrorEntidadExistsTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad-02");
		request.setDescripcion("Descripcion");
		
		// when
		ResponseEntity<ErrorEntity> response = rest.exchange(
				"/entidades/" + id,
				HttpMethod.PUT,
				new HttpEntity<>(request),
				ErrorEntity.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(response.getBody().getError()).isEqualTo("entidad_exists");
		assertThat(response.getBody().getMensaje()).isEqualTo("El nombre '" + request.getNombre() + "' ya existe.");	
	}
	
	@Test
	public void modificarErrorDataTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000001";
		EntidadCommand request = new EntidadCommand();
		request.setNombre(null);
		request.setDescripcion("");
		
		// when
		ResponseEntity<DataErrorEntity> response = rest.exchange(
				"/entidades/" + id,
				HttpMethod.PUT,
				new HttpEntity<>(request),
				DataErrorEntity.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
		assertThat(response.getBody().getError()).isEqualTo("invalid_data");
		assertThat(response.getBody().getCampos())
			.anyMatch(c -> "nombre".equals(c.getCampo()) && "not_blank".equals(c.getError()))
			.anyMatch(c -> "descripcion".equals(c.getCampo()) && "not_blank".equals(c.getError()))
			.size().isEqualTo(2);
	}
	
	@Test
	public void modificarNotFoundTest() {
		
		// given
		String id = "00000000-0000-0000-0000-000000000000";
		EntidadCommand request = new EntidadCommand();
		request.setNombre("Entidad-00");
		request.setDescripcion("Descripción");

		// when
		ResponseEntity<DataErrorEntity> response = rest.exchange(
				"/entidades/" + id,
				HttpMethod.PUT,
				new HttpEntity<>(request),
				DataErrorEntity.class);
		
		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody().getError()).isEqualTo("not_found");
		assertThat(response.getBody().getMensaje()).isEqualTo("No se encontró el recurso solicitado");
	}

}
