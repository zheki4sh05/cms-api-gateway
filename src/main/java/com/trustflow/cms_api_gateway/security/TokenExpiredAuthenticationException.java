package com.trustflow.cms_api_gateway.security;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredAuthenticationException extends AuthenticationException {

	public TokenExpiredAuthenticationException(Throwable cause) {
		super("TOKEN_EXPIRED", cause);
	}
}
