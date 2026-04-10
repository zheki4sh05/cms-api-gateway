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
						.path("/auth/**")
						.filters(f -> f.removeRequestHeader("Authorization"))
						.uri(authBaseUrl)
				)
				.route("cms-company-info", r -> r
						.path("/api/v1/company", "/api/v1/company/**")
						.filters(f -> f.rewritePath("/api/v1/company(?<segment>/?.*)", "/company${segment}"))
						.uri(companyInfoBaseUrl)
				)
				.route("cms-company-info-companies", r -> r
						.path("/api/v1/companies", "/api/v1/companies/**")
						.filters(f -> f.rewritePath("/api/v1/companies(?<segment>/?.*)", "/companies${segment}"))
						.uri(companyInfoBaseUrl)
				)
				.route("cms-company-info-departments", r -> r
						.path("/api/v1/departments", "/api/v1/departments/**")
						.filters(f -> f.rewritePath("/api/v1/departments(?<segment>/?.*)", "/departments${segment}"))
						.uri(companyInfoBaseUrl)
				)
				.route("cms-company-info-invitations-send", r -> r
						.path("/api/v1/invitations/send", "/api/v1/invitations/send/**")
						.filters(f -> f.rewritePath("/api/v1/invitations/send(?<segment>/?.*)", "/invitations/send${segment}"))
						.uri(companyInfoBaseUrl)
				)
				.build();
	}
}
