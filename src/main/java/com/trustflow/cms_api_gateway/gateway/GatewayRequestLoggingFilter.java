package com.trustflow.cms_api_gateway.gateway;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Component
@ConditionalOnProperty(name = "app.gateway.logging.requests-enabled", havingValue = "true", matchIfMissing = true)
public class GatewayRequestLoggingFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

	static final String REQUEST_ID_ATTR = GatewayRequestLoggingFilter.class.getName() + ".requestId";
	static final String START_NANO_ATTR = GatewayRequestLoggingFilter.class.getName() + ".startNano";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		long startNano = System.nanoTime();

		String requestId = request.getHeaders().getFirst("X-Request-Id");
		if (requestId == null || requestId.isBlank()) {
			requestId = UUID.randomUUID().toString();
		}
		exchange.getAttributes().put(REQUEST_ID_ATTR, requestId);
		exchange.getAttributes().put(START_NANO_ATTR, startNano);
		exchange.getResponse().getHeaders().set("X-Request-Id", requestId);

		String method = request.getMethod() != null ? request.getMethod().name() : "-";
		String path = request.getURI().getRawPath();

		log.debug("request begin requestId={} {} {}", requestId, method, path);

		return chain.filter(exchange).doFinally(signal -> logCompleted(exchange, method, path, signal));
	}

	private static void logCompleted(ServerWebExchange exchange, String method, String path, SignalType signal) {
		Long startNanoObj = exchange.getAttribute(START_NANO_ATTR);
		long durationMs = startNanoObj != null
				? TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanoObj)
				: -1L;

		String requestId = exchange.getAttribute(REQUEST_ID_ATTR);
		if (requestId == null) {
			requestId = "-";
		}

		var status = exchange.getResponse().getStatusCode();
		String statusStr = status != null ? status.toString() : "-";

		Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		String routeId = route != null ? route.getId() : "-";

		log.info(
				"request end requestId={} {} {} -> {} {}ms route={} signal={}",
				requestId,
				method,
				path,
				statusStr,
				durationMs,
				routeId,
				signal
		);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
