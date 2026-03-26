package com.appasistencia.configurations;

import com.appasistencia.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Filtro JWT: intercepta cada request, valida token y setea autenticacion
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Buscar header Authorization con formato "Bearer <token>"
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token sin el prefijo "Bearer "
        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer claims del token (email, rol, ids)
        Claims claims = jwtService.extractClaims(token);
        String email = claims.getSubject();
        String rol = claims.get("rol", String.class);
        Long idInstitucion = claims.get("idInstitucion", Long.class);
        Long idUsuario = claims.get("idUsuario", Long.class);

        // Inyectar datos del usuario en atributos del request para los controllers
        request.setAttribute("idInstitucion", idInstitucion);
        request.setAttribute("idUsuario", idUsuario);
        request.setAttribute("email", email);

        // Crear token de autenticacion con rol del usuario
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol))
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
