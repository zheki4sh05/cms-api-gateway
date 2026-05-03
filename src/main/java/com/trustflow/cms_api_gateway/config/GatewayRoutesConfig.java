package com.trustflow.cms_api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouteLocator customRouteLocator(
			RouteLocatorBuilder builder,
			@Value("${app.services.auth.base-url}") String authBaseUrl,
			@Value("${app.services.company-info.base-url}") String companyInfoBaseUrl,
			@Value("${app.services.monitoring.base-url}") String monitoringBaseUrl,
			@Value("${app.services.risk.base-url}") String riskBaseUrl,
			@Value("${app.services.workflow.base-url}") String workflowBaseUrl
	) {
		return builder.routes()
				.route("auth-user-me", r -> r
						.path("/api/me", "/api/me/**")
						.filters(f -> f.rewritePath("/api/me(?<segment>/?.*)", "/api/users/me${segment}"))
						.uri(authBaseUrl)
				)
				.route("auth-users", r -> r
						.path("/api/users", "/api/users/**")
						.uri(authBaseUrl)
				)
				.route("auth-api-prefix", r -> r
						.path("/api/auth/**")
						.filters(f -> f
								.removeRequestHeader("Authorization")
								.rewritePath("/api/auth(?<segment>/?.*)", "/auth${segment}")
						)
						.uri(authBaseUrl)
				)
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
				.route("monitoring-risk-objects", r -> r
						.path("/api/risk-objects", "/api/risk-objects/**")
						.uri(monitoringBaseUrl)
				)
				.route("monitoring-integration-configs-change-history", r -> r
						.path("/api/integration-configs/change-history", "/api/integration-configs/change-history/**")
						.uri(monitoringBaseUrl)
				)
				.route("monitoring-integration-configs", r -> r
						.path("/api/integration-configs", "/api/integration-configs/**")
						.uri(monitoringBaseUrl)
				)
				.route("monitoring-risk-object-model-by-id", r -> r
						.path("/api/risk-object-models/*")
						.filters(f -> f.rewritePath("/api/risk-object-models/(?<id>[^/]+)", "/api/risk-objects/${id}"))
						.uri(monitoringBaseUrl)
				)
				.route("monitoring-risk-object-models", r -> r
						.path("/api/risk-object-models", "/api/risk-object-models/**")
						.uri(monitoringBaseUrl)
				)
				.route("cms-risk-rules", r -> r
						.path("/api/risks", "/api/risks/**")
						.filters(f -> f.rewritePath("/api/risks(?<segment>/?.*)", "/api/rules${segment}"))
						.uri(riskBaseUrl)
				)
				.route("cms-risk-rule-risk-object-put", r -> r
						.method(HttpMethod.PUT)
						.and()
						.path("/api/rules/*/risk-object")
						.uri(riskBaseUrl)
				)
				.route("cms-risk-rules-direct", r -> r
						.path("/api/rules", "/api/rules/**")
						.uri(riskBaseUrl)
				)
				.route("cms-risk-rules-change-history", r -> r
						.path("/api/rules/change-history", "/api/rules/change-history/**")
						.uri(riskBaseUrl)
				)
				.route("cms-risk-categories", r -> r
						.path("/api/risk-categories", "/api/risk-categories/**")
						.uri(riskBaseUrl)
				)
				.route("cms-workflow-action-plans", r -> r
						.path("/api/action-plans", "/api/action-plans/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-action-plans-v1", r -> r
						.path("/api/v1/action-plans", "/api/v1/action-plans/**")
						.filters(f -> f.rewritePath("/api/v1/action-plans(?<segment>/?.*)", "/api/action-plans${segment}"))
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-incidents-my", r -> r
						.path("/api/v1/incidents/my", "/api/v1/incidents/my/**")
						.filters(f -> f.rewritePath("/api/v1/incidents/my(?<segment>/?.*)", "/api/incidents/my${segment}"))
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-incident-view", r -> r
						.path("/api/incidents/*/view")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-incident-assign-to-me", r -> r
						.path("/api/incidents/*/assign-to-me")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-cases-my", r -> r
						.path("/api/v1/cases/my", "/api/v1/cases/my/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-cases-view", r -> r
						.path("/api/v1/cases/view", "/api/v1/cases/view/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-view-by-id", r -> r
						.path("/api/v1/cases/*/view", "/api/v1/cases/*/view/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-comments-v1", r -> r
						.path("/api/v1/cases/*/comments", "/api/v1/cases/*/comments/**")
						.filters(f -> f.rewritePath(
								"/api/v1/cases/(?<caseId>[^/]+)/comments(?<segment>/?.*)",
								"/api/cases/${caseId}/comments${segment}"
						))
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-attachments-v1", r -> r
						.path("/api/v1/cases/*/attachments", "/api/v1/cases/*/attachments/**")
						.filters(f -> f.rewritePath(
								"/api/v1/cases/(?<caseId>[^/]+)/attachments(?<segment>/?.*)",
								"/api/cases/${caseId}/attachments${segment}"
						))
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-investigation", r -> r
						.path("/api/cases/*/investigation", "/api/cases/*/investigation/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-comments", r -> r
						.path("/api/cases/*/comments", "/api/cases/*/comments/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-attachment-download", r -> r
						.path("/api/cases/*/attachments/*/download", "/api/cases/*/attachments/*/download/**")
						.uri(workflowBaseUrl)
				)
				.route("cms-workflow-case-attachment-by-id", r -> r
						.path("/api/cases/*/attachments/*")
						.uri(workflowBaseUrl)
				)
				.build();
	}
}
