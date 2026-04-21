package io.cinema.converter;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@NoArgsConstructor
public class JwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        var claims = source.getClaims();

        var realmAccess = claims.get("realm_access");
        var realmRoles = extractRoles(realmAccess);

        Set<String> rolesSet = new HashSet<>(realmRoles);



        Object resourceAccess = claims.get("resource_access");
        if (resourceAccess instanceof Map<?, ?> rsrc) {
            rsrc.values().forEach(value -> {
                var resourceRole = extractRoles(value);
                rolesSet.addAll(resourceRole);
            });
        }

        log.debug("Roles fetched: {}", rolesSet);

        return rolesSet.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Collection<String> extractRoles(Object accessObject){
        if (
                accessObject instanceof Map<?, ?> accessMap &&
                accessMap.get("roles") instanceof Collection<?> roles
        ) {
            return roles.stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

}
