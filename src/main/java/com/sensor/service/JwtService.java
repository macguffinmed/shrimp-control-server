package com.sensor.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {
    private final SecretKey key;
    private final ConcurrentHashMap<String, Long> revokedUntilEpochSecond = new ConcurrentHashMap<>();

    public JwtService(@Value("${security.jwt.secret:}") String secret) {
        if (secret != null && !secret.isEmpty()) {
            byte[] bytes = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(bytes);
        } else {
            byte[] random = new byte[32];
            new SecureRandom().nextBytes(random);
            this.key = Keys.hmacShaKeyFor(random);
        }
    }

    public String issue(String subject, Map<String, Object> claims, long ttlSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> parse(String token) {
        Long until = revokedUntilEpochSecond.get(token);
        if (until != null) {
            long now = Instant.now().getEpochSecond();
            if (now < until) {
                throw new RuntimeException("token revoked");
            }
            revokedUntilEpochSecond.remove(token);
        }
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public void revoke(String token) {
        if (token == null || token.trim().isEmpty()) {
            return;
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        Date exp = claims.getExpiration();
        long until = exp != null ? exp.toInstant().getEpochSecond() : Instant.now().getEpochSecond();
        revokedUntilEpochSecond.put(token, until);
    }
}

