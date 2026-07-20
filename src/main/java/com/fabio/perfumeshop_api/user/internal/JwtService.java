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

    /*
    * Extrae el email de un token.
    * Si el token es inválido o está manipulado, lanza una excepción al parsearlo.
    * */
    String extractEmail(String token){
        return parseClaims(token).getSubject();

    }

    //Extrae el rol del claim.
    String extractRole(String token){
        return parseClaims(token).get("role", String.class);

    }

    /*
    *Comprueba si un token es válido: firma correcta y no caducado.
    * Si el parseo falla devuelve false.
    * */
    boolean isValid(String token){
        try {
            parseClaims(token);
            return true;
        }catch (Exception ex){
            return false;
        }

    }

    /**
     * Parsea y verifica el token con la clave secreta.
     * Si la firma no coincide o está caducado, jjwt lanza excepción aquí.
     * Este método concentra la verificación; los demás se apoyan en él.
     */
    private io.jsonwebtoken.Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }



}
