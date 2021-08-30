package com.mroncatto.startup;

import com.mroncatto.startup.constant.SecurityConstant;
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

import static com.mroncatto.startup.constant.SecurityConstant.COMPANY_DESC;
import static com.mroncatto.startup.constant.SecurityConstant.COMPANY_NAME;

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
				.info(new Info().title(COMPANY_NAME).version(appVersion)
						.description(COMPANY_DESC)
						.license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
	}


}
