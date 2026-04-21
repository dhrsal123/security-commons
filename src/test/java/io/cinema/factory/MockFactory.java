package io.cinema.factory;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@UtilityClass
public class MockFactory {

    public static Jwt buildJwt() {
        return buildBaseJwt()
                .header("something", "hey")
                .claim("realm_access", Map.of("roles", List.of("admin")))
                .claim(
                        "resource_access",
                        Map.of(
                                "resource", Map.of("roles", List.of("ROLE_employee")))
                )
                .build();

    }

    public static Jwt.Builder buildBaseJwt() {
        return Jwt.withTokenValue("eyJhbGciOiJIUzI1NiJ9")
                .audience(List.of("audience-test"));

    }

    public static Stream<Arguments> buildEmptyJwts() {
        var emptyMap = buildBaseJwt()
                .header("something", "hey")
                .claim("realm_access", Map.of())
                .claim("resource_access", Map.of())
                .build();

        var differentClaims = buildBaseJwt()
                .header("something", "hey")
                .claim("realm_access", Map.of())
                .claim("resource_access", List.of())
                .build();

        return Stream.of(
                Arguments.of(emptyMap),
                Arguments.of(differentClaims)
                );
    }


}
