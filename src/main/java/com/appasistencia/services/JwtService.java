package com.appasistencia.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Servicio: generacion y validacion de tokens JWT
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    // Generar token con claims del usuario (rol, institucion, nombre)
    public String generateToken(Long idUsuario, String email, String rol,
                                Long idInstitucion, String nombre, String apellido) {
        return Jwts.builder()
                .subject(email)
                .claim("idUsuario", idUsuario)
                .claim("rol", rol)
                .claim("idInstitucion", idInstitucion)
                .claim("nombre", nombre)
                .claim("apellido", apellido)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    // Validar token y extraer datos (claims)
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractIdInstitucion(String token) {
        return extractClaims(token).get("idInstitucion", Long.class);
    }

    public Long extractIdUsuario(String token) {
        return extractClaims(token).get("idUsuario", Long.class);
    }

    public String extractNombreCompleto(String token) {
        Claims claims = extractClaims(token);
        String nombre = claims.get("nombre", String.class);
        String apellido = claims.get("apellido", String.class);
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");
    }

    // Verificar si el token no expiro
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
