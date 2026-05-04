package io.cinema.config;

import io.cinema.converter.JwtConverter;
import io.cinema.domain.dto.Cors;
import io.cinema.filters.SpaWebFilter;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Objects;

import static io.cinema.common.Constants.PERMISSIONS_POLICY;


@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final AppProperties appProperties;
    private final JwtConverter converter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers ->
                        headers
                                .referrerPolicy(
                                        referrer -> referrer.policy(
                                                ReferrerPolicyServerHttpHeadersWriter.
                                                        ReferrerPolicy.
                                                        STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                        )
                                )
                                .permissionsPolicy(permissions ->
                                        permissions.policy(PERMISSIONS_POLICY)
                                )
                                .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                )
                .addFilterAt(new SpaWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/webjars/**",
                                        "/swagger",
                                        "/openapi.yaml"
                                ).permitAll()
                                .pathMatchers("/api/csrf", "/health").permitAll()
                                .pathMatchers("/", "/*.hmtl", "/*.js", "/*.css", "/*.png", "/*.jpg").permitAll()
                                .anyExchange().authenticated()
                ).oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                        .jwkSetUri(appProperties.getOAuth2().getJwkSetUri())
                        )
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        Cors cors = appProperties.getCors();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(cors.getAllowedOrigins());
        config.setAllowedHeaders(cors.getAllowedHeaders());
        config.setAllowedMethods(cors.getAllowedMethods());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                jwt -> Flux.fromIterable(Objects.requireNonNull(converter.convert(jwt)))
        );

        return jwtAuthenticationConverter;

    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        try {
            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient client = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext));

            WebClient webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(client))
                    .build();

            return NimbusReactiveJwtDecoder
                    .withJwkSetUri(appProperties.getOAuth2().getJwkSetUri())
                    .webClient(webClient)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    //private methods
    private Mono<Void> csrfWebFilter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<CsrfToken> token = exchange.getAttribute(CsrfToken.class.getName());
        return Objects.nonNull(token) ? token.then(chain.filter(exchange)) : chain.filter(exchange);
    }
}
