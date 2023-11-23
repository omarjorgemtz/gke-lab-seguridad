package mx.com.gnp.seguridad.config;

import java.io.InputStream;
import java.util.Optional;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import mx.com.gnp.cuentas.application.config.CuentasAutoConfiguration;
import mx.com.gnp.cuentas.application.model.CuentasJwtBody;
import mx.com.gnp.security.jwt.extractor.TokenExtractor;
import mx.com.gnp.security.jwt.parser.JwtParser;

@TestConfiguration
@EnableAutoConfiguration(exclude = CuentasAutoConfiguration.class)
public class CuentasSecurityTestConfig {
	
	private static final String YAML_FILE = "cuentas-jwt-body.yaml";
	
	@Bean
	public JwtParser<CuentasJwtBody> jwtParser() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		InputStream in = new ClassPathResource(YAML_FILE, CuentasSecurityTestConfig.class).getInputStream();
		CuentasJwtBody jwt = mapper.readValue(in, CuentasJwtBody.class);
		return new StaticBodyJwtParser(jwt);
	}
	
	@Bean
	public TokenExtractor tokenExtractor() {
		return req -> Optional.of("TOKEN");
	}
	
	class StaticBodyJwtParser extends JwtParser<CuentasJwtBody> {
		
		private CuentasJwtBody body;
		
		public StaticBodyJwtParser(CuentasJwtBody body) {
			super(CuentasJwtBody.class);
			this.body = body;
			this.setRolesExtractor(jwt -> jwt.getClaims().getRoles());
			this.setUsernameExtractor(jwt -> jwt.getUserId());
		}

		@Override
		protected CuentasJwtBody getClaims(String token) {
			return body;
		}
		
	}

}




