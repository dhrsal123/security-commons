package io.cinema.filters;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpaWebFilterTest {

    private SpaWebFilter spaWebFilter;

    @Mock
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        spaWebFilter = new SpaWebFilter();
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/dashboard",
            "/users/profile",
            "/settings",
            "/"
    })
    void shouldMutatePathToIndexHtmlWhenPathIsValidSpaRoute(String path) {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(path).build());

        // Act
        StepVerifier.create(spaWebFilter.filter(exchange, chain))
                .verifyComplete();

        // Assert
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(exchangeCaptor.capture());

        ServerWebExchange mutatedExchange = exchangeCaptor.getValue();
        assertEquals("/index.html", mutatedExchange.getRequest().getURI().getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/users",
            "/management/health",
            "/login",
            "/services/auth",
            "/swagger-ui.html",
            "/v2/api-docs",
            "/v3/api-docs",
            "/main.js",
            "/styles/theme.css",
            "/assets/logo.png",
            "/favicon.ico"
    })
    void shouldNotMutatePathWhenPathMatchesIgnoredPrefixesOrHasADot(String path) {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(path).build());

        // Act
        StepVerifier.create(spaWebFilter.filter(exchange, chain))
                .verifyComplete();

        // Assert
        ArgumentCaptor<ServerWebExchange> exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(exchangeCaptor.capture());

        ServerWebExchange capturedExchange = exchangeCaptor.getValue();
        assertEquals(path, capturedExchange.getRequest().getURI().getPath(), "Path should remain unchanged");
    }

}