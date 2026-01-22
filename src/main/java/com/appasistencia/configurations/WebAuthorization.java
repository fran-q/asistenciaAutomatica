package com.appasistencia.configurations;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@EnableWebSecurity
@Configuration
public class WebAuthorization {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Páginas públicas
                        .requestMatchers(
                                "/",
                                "/web/index.html",
                                "/web/login.html",
                                "/web/registro.html",
                                "/web/css/**",
                                "/web/js/**",
                                "/web/img/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Registro público (crear cuenta)
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()

                        // Vista de no autorizado si la usás
                        .requestMatchers("/web/no-autorizado.html").permitAll()

                        // Paneles de administración: sólo ADMINISTRADOR
                        .requestMatchers(
                                "/web/usuarios.html",
                                "/web/usuario-form.html",
                                "/web/asistencias.html",
                                "/web/asistencia-form.html",
                                "/web/usuario-asistencias.html"
                        ).hasAuthority("ADMINISTRADOR")

                        // API: sólo ADMINISTRADOR para gestionar datos
                        .requestMatchers("/api/usuarios/**", "/api/asistencias/**")
                        .hasAuthority("ADMINISTRADOR")

                        // Este sólo requiere sesión (lo usan login.js y menu.js)
                        .requestMatchers("/api/usuarios/current").authenticated()

                        .anyRequest().denyAll()
                )
                .formLogin(form -> form
                        // El frontend manda el login a /api/login con email y password
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((req, res, auth) -> {
                            // Si llegó acá, las credenciales son correctas
                            res.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((req, res, exc) -> {
                            // Credenciales inválidas
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                );

        return http.build();
    }
}
