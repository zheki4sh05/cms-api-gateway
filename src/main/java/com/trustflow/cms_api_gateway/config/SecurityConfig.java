package com.trustflow.cms_api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;

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
						.pathMatchers(
								"/auth/login",
								"/auth/login/**",
								"/auth/registration",
								"/auth/registration/**"
						).permitAll()
						.pathMatchers(HttpMethod.OPTIONS).permitAll()
						.anyExchange().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.authenticationEntryPoint(tokenExpiredAwareAuthenticationEntryPoint)
						.jwt(jwt -> jwt.authenticationManager(jwtReactiveAuthenticationManager))
				)
				.build();
	}
}
