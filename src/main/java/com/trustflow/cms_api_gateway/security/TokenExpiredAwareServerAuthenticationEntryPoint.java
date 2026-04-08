package com.trustflow.cms_api_gateway.security;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class TokenExpiredAwareServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

	private final BearerTokenServerAuthenticationEntryPoint delegate = new BearerTokenServerAuthenticationEntryPoint();

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		if (isTokenExpired(ex)) {
			var response = exchange.getResponse();
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
			byte[] body = "TOKEN_EXPIRED".getBytes(StandardCharsets.UTF_8);
			DataBuffer buffer = response.bufferFactory().wrap(body);
			return response.writeWith(Mono.just(buffer));
		}
		return delegate.commence(exchange, ex);
	}

	private static boolean isTokenExpired(AuthenticationException ex) {
		for (Throwable cur = ex; cur != null; cur = cur.getCause()) {
			if (cur instanceof TokenExpiredAuthenticationException) {
				return true;
			}
		}
		return false;
	}
}
