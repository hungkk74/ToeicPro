package com.toeic.gateway.web.rest;

import java.util.Map;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;


@RestController
public class LogoutResource {

    private final ReactiveClientRegistrationRepository registrationRepository;

    public LogoutResource(ReactiveClientRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    /**
     * {@code POST  /api/logout} : logout the current user.
     *
     * @param oAuth2AuthenticationToken the OAuth2 authentication token.
     * @param oidcUser the OIDC user.
     * @param request a {@link ServerHttpRequest} request.
     * @param session the current {@link WebSession}.
     * @return status {@code 200 (OK)} and a body with a global logout URL.
     */
    @PostMapping("/api/logout")
    public Mono<Map<String, String>> logout(
        @CurrentSecurityContext(expression = "authentication") OAuth2AuthenticationToken oAuth2AuthenticationToken,
        @AuthenticationPrincipal OidcUser oidcUser,
        ServerHttpRequest request,
        WebSession session
    ) {
        return session
            .invalidate()
            .then(
                registrationRepository
                    .findByRegistrationId(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())
                    .map(oidc -> prepareLogoutUri(request, oidc, oidcUser.getIdToken()))
            );
    }

    private Map<String, String> prepareLogoutUri(ServerHttpRequest request, ClientRegistration clientRegistration, OidcIdToken idToken) {
        StringBuilder logoutUrl = new StringBuilder();

        logoutUrl.append(clientRegistration.getProviderDetails().getConfigurationMetadata().get("end_session_endpoint").toString());

        String originUrl = request.getHeaders().getOrigin();
        if (originUrl == null) {
            var requestUri = request.getURI();
            originUrl = requestUri.getScheme() + "://" + requestUri.getAuthority();
        }

        logoutUrl.append("?id_token_hint=").append(idToken.getTokenValue()).append("&post_logout_redirect_uri=").append(originUrl);

        return Map.of("logoutUrl", logoutUrl.toString());
    }
}
