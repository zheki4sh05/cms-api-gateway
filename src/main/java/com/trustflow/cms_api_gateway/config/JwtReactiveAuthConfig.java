package com.trustflow.cms_api_gateway.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;

import com.trustflow.cms_api_gateway.security.TokenExpiredAuthenticationException;
import com.trustflow.cms_api_gateway.security.TokenExpiredAwareServerAuthenticationEntryPoint;

@Configuration
public class JwtReactiveAuthConfig {

	@Bean
	public ReactiveAuthenticationManager jwtReactiveAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
		JwtReactiveAuthenticationManager delegate = new JwtReactiveAuthenticationManager(jwtDecoder);
		return authentication -> delegate.authenticate(authentication)
				.onErrorMap(JwtReactiveAuthConfig::mapIfExpiredAccessToken);
	}

	@Bean
	public ServerAuthenticationEntryPoint tokenExpiredAwareAuthenticationEntryPoint() {
		return new TokenExpiredAwareServerAuthenticationEntryPoint();
	}

	private static Throwable mapIfExpiredAccessToken(Throwable ex) {
		if (isAccessTokenExpired(ex)) {
			return new TokenExpiredAuthenticationException(ex);
		}
		return ex;
	}

	private static boolean isAccessTokenExpired(Throwable ex) {
		for (Throwable cur = ex; cur != null; cur = cur.getCause()) {
			if (!(cur instanceof JwtException)) {
				continue;
			}
			String msg = cur.getMessage();
			if (msg != null && msg.toLowerCase(Locale.ROOT).contains("expired")) {
				return true;
			}
		}
		return false;
	}
}
