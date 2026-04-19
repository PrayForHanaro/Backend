package com.hanaro.apigateway.security;

import java.util.Collection;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        Collection<GrantedAuthority> authorities =
                roles == null
                        ? List.of()
                        : roles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .map(authority -> (GrantedAuthority) authority)
                        .toList();

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}