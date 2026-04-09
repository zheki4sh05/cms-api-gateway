package com.trustflow.cms_api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(
			ServerHttpSecurity http,
			ReactiveAuthenticationManager jwtReactiveAuthenticationManager,
			ServerAuthenticationEntryPoint tokenExpiredAwareAuthenticationEntryPoint
	) {
		return http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/actuator/health").permitAll()
						.pathMatchers("/auth/**").permitAll()
						.pathMatchers(HttpMethod.OPTIONS).permitAll()
						.anyExchange().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.authenticationEntryPoint(tokenExpiredAwareAuthenticationEntryPoint)
						.bearerTokenConverter(this::convertBearerTokenUnlessAuthPath)
						.jwt(jwt -> jwt.authenticationManager(jwtReactiveAuthenticationManager))
				)
				.build();
	}

	private Mono<org.springframework.security.core.Authentication> convertBearerTokenUnlessAuthPath(
			ServerWebExchange exchange
	) {
		String path = exchange.getRequest().getPath().pathWithinApplication().value();
		if ("/auth".equals(path) || path.startsWith("/auth/")) {
			return Mono.empty();
		}
		ServerBearerTokenAuthenticationConverter delegate = new ServerBearerTokenAuthenticationConverter();
		try {
			return delegate.convert(exchange);
		} catch (AuthenticationServiceException ex) {
			return Mono.error(ex);
		}
	}
}
