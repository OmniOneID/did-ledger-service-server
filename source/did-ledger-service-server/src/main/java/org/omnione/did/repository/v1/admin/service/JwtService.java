package org.omnione.did.repository.v1.admin.service;

import org.omnione.did.base.property.JwtProperty;
import org.omnione.did.repository.v1.admin.dto.admin.AdminDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtService {
    private final JwtProperty props;

    private Key key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(AdminDto admin) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(admin.getId().toString())
                .claim("loginId", admin.getLoginId())
                .claim("role", admin.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getAccessTtlSeconds())))
                .signWith(key())
                .compact();
    }

    public String createRefreshToken(Long adminId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(adminId.toString())
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getRefreshTtlSeconds())))
                .signWith(key())
                .compact();
    }

    public Jws<Claims> parse(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwt);
    }

    public boolean isRefreshToken(Jws<Claims> jws) {
        return "refresh".equals(jws.getPayload().get("typ", String.class));
    }
}
