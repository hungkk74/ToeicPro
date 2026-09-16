package com.toeic.gateway.web.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

class OAuth2ReactiveRefreshTokensWebFilterTest {

    @Test
    void shouldNotRefreshTokenWhenLoggingOut() {
        ReactiveOAuth2AuthorizedClientManager clientManager = mock(ReactiveOAuth2AuthorizedClientManager.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/logout").build());
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        new OAuth2ReactiveRefreshTokensWebFilter(clientManager).filter(exchange, chain).block();

        verify(chain).filter(exchange);
        verifyNoInteractions(clientManager);
    }
}
