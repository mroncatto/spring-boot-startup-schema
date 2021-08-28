package com.mroncatto.startup;

import com.google.common.cache.LoadingCache;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class StartupApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartupApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Swagger
	@Bean
	public OpenAPI customOpenAPI(@Value("1.0.0") String appVersion) {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("bearerAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer")))
				.info(new Info().title("YOUR APP NAME").version(appVersion)
						.description("YOUR APP DESCRIPTION")
						.license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
	}


}
