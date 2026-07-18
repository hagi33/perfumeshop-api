package com.fabio.perfumeshop_api.user.internal;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
class JwtService {

    private final SecretKey secretKey;
    private final long expiration;


    JwtService(@Value("${jwt.secret}") String secret,
               @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }


    String generateToken(String email, Role role){
        Date now = new Date();
        Date expires = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expires)
                .signWith(secretKey)
                .compact();

    }
}
