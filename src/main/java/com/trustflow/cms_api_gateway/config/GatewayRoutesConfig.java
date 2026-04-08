package com.trustflow.cms_api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouteLocator customRouteLocator(
			RouteLocatorBuilder builder,
			@Value("${app.services.auth.base-url}") String authBaseUrl,
			@Value("${app.services.company-info.base-url}") String companyInfoBaseUrl
	) {
		return builder.routes()
				.route("auth-login-and-registration", r -> r
						.path(
								"/auth/login",
								"/auth/login/**",
								"/auth/registration",
								"/auth/registration/**"
						)
						.uri(authBaseUrl)
				)
				.route("cms-company-info", r -> r
						.path("/api/v1/company", "/api/v1/company/**")
						.uri(companyInfoBaseUrl)
				)
				.build();
	}
}
