package io.cinema.converter;

import io.cinema.factory.MockFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtConverterTest {
    @InjectMocks
    private JwtConverter jwtConverter;

    private static Stream<Arguments> provideJWTWithEmptyClaims() {
        return MockFactory.buildEmptyJwts();
    }

    @Test
    void shouldConvertJwt() {
        // Arrange
        var jwt = MockFactory.buildJwt();

        // Act
        var response = jwtConverter.convert(jwt);

        // Assert
        var resp = response.stream().toList();
        assertTrue(resp.contains(new SimpleGrantedAuthority("ROLE_admin")));
        assertTrue(resp.contains(new SimpleGrantedAuthority("ROLE_employee")));
    }

    @Test
    void shouldFailWhenTokenIsInvalid() {
        // Act
        var response = assertThrows(NullPointerException.class, () -> jwtConverter.convert(null));

        // Assert
        assertEquals(
                "Cannot invoke \"org.springframework.security.oauth2.jwt.Jwt.getClaims()\" because \"source\" is null",
                response.getMessage()
        );
    }

    @ParameterizedTest
    @MethodSource("provideJWTWithEmptyClaims")
    void shouldSucceedWhenClaimsAreEmpty(Jwt jwt) {
        // Act
        var response = jwtConverter.convert(jwt);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }


}